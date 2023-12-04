//Copyright 2022 Artiom Mirolyubov
//Licensed under the Apache License, Version 2.0
package com.company.controller.listener.menu;

import com.company.config.ConfigFile;
import com.company.model.BodyParser;
import com.company.model.process.ProcessFile;
import com.company.model.NavObject;
import com.company.model.process.ProcessObject;
import com.company.view.MainFrame;
import com.company.view.ProgressBar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileListener implements ActionListener {
    private final MainFrame mainFrame;
    private final int CAPACITY = 10;
    private final int THREADS = 2;

    public FileListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case ("Close"):
                mainFrame.close();
                break;
            case "Open":
                openFile();
                break;
            default:
                processFile(command);
                break;
        }
    }

    private void openFile() {
        JFileChooser fileOpen = new JFileChooser();
        int ret = fileOpen.showDialog(null, "Open File");
        if (ret == JFileChooser.APPROVE_OPTION) {
            ConfigFile configFile = ConfigFile.getInstance();
            String path = fileOpen.getSelectedFile().getAbsolutePath();
            configFile.setRecentFile(path);
            File file = new File(path);
            processFile(file.getName());
        }
    }

    private void processFile(String command) {
        BlockingQueue<NavObject> navQueue = new ArrayBlockingQueue<>(CAPACITY);
        ConfigFile configFile = ConfigFile.getInstance();
        ArrayList<String> recentFiles = configFile.getRecentFiles();
        for (String path : recentFiles) {
            File file = new File(path);
            if(file.getName().equals(command)) {
                if (file.exists()) {
                    ProcessFile process = new ProcessFile(navQueue, file, mainFrame);
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
                    JOptionPane.showMessageDialog(mainFrame, fileNotExists);
                }
            }
        }
    }
}
