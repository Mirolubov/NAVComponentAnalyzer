package com.company.model.process;

import com.company.config.ConfigFile;
import com.company.model.NavObject;
import com.company.model.NavObjects;
import com.company.view.MainFrame;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileLoader {
    private final MainFrame mainFrame;
    private NavObjects navObjects;
    private String command;
    private final int CAPACITY = 10;
    private final int THREADS = 2;

    public FileLoader(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.navObjects = mainFrame.getNavObjects();
    }

    public FileLoader(String path, MainFrame mainFrame, NavObjects navObjects) {
        this.mainFrame = mainFrame;
        this.navObjects = navObjects;
        ConfigFile configFile = ConfigFile.getInstance();
        configFile.setRecentFile(path);
        File file = new File(path);
        this.command = file.getName();
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void processFile() {
        System.out.println("loading file: " + command);
        BlockingQueue<NavObject> navQueue = new ArrayBlockingQueue<>(CAPACITY);
        ConfigFile configFile = ConfigFile.getInstance();
        ArrayList<String> recentFiles = configFile.getRecentFiles();
        for (String path : recentFiles) {
            File file = new File(path);
            if(file.getName().equals(command)) {
                if (file.exists()) {
                    ProcessFile process = new ProcessFile(navQueue, file, mainFrame, navObjects);
                    Thread fileThread = new Thread(process);
                    fileThread.start();
                    for(int i = 0; i < THREADS; i ++) {
                        ProcessObject processObject = new ProcessObject(navQueue);
                        Thread objThread = new Thread(processObject);
                        objThread.start();
                    }
                } else {
                    String fileNotExists = String.format("File %s not exists", path);
                    System.out.println(fileNotExists);
                    if(mainFrame != null) {
                        JOptionPane.showMessageDialog(mainFrame, fileNotExists);
                    }
                }
            }
        }
    }

    public void clearNavObjects() {
        navObjects.removaAll();
    }
}
