package com.company.navcomponentanalyzer.core.model.process;

import com.company.navcomponentanalyzer.core.config.ConfigFile;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.NavObjects;
import com.company.navcomponentanalyzer.core.model.object.NavType;
import com.company.navcomponentanalyzer.core.view.MainFrame;

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
        System.out.print("loading file: " + command + " ... -");
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
                    if(mainFrame == null) {
                        try {
                            int n = 0;
                            while (navProccessInWork(navQueue)) {
                                System.out.print("\b");
                                switch (n) {
                                    case 0:
                                        System.out.print("\\");
                                        break;
                                    case 1:
                                        System.out.print("|");
                                        break;
                                    case 2:
                                        System.out.print("/");
                                        break;
                                    case 3:
                                        System.out.print("-");
                                        break;
                                }
                                n++;
                                if (n == 4) {
                                    n = 0;
                                }
                                Thread.sleep(300);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("\bDone.");
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

    private boolean navProccessInWork(BlockingQueue<NavObject> navQueue) throws InterruptedException {
        NavObject navObject = navQueue.take();
        if (navObject.getId() == 0) {
            navObject = new NavObject(0, "", NavType.Table);
            navQueue.put(navObject);
            return false;
        } else {
            navQueue.put(navObject);
            return true;
        }
    }

    public void clearNavObjects() {
        navObjects.removaAll();
    }
}
