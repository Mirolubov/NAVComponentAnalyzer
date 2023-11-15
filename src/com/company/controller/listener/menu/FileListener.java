//Copyright 2022 Artiom Mirolyubov
//Licensed under the Apache License, Version 2.0
package com.company.controller.listener.menu;

import com.company.config.ConfigFile;
import com.company.model.BodyParser;
import com.company.controller.ProcessFile;
import com.company.view.MainFrame;
import com.company.view.ProgressBar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class FileListener implements ActionListener {
    private final MainFrame mainFrame;

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
        ConfigFile configFile = ConfigFile.getInstance();
        ArrayList<String> recentFiles = configFile.getRecentFiles();
        for (String path : recentFiles) {
            File file = new File(path);
            if(file.getName().equals(command)) {
                if (file.exists()) {
                    ProcessFile process = new ProcessFile();
                    process.process(file);
                    mainFrame.setNavObjects(process.getNavObjects());

                    ProgressBar progressBar = new ProgressBar(process.getNavObjects().getCount(), mainFrame);
                    progressBar.setVisible(true);
                    Thread progressBarThread = new Thread(progressBar);
                    progressBarThread.start();

                    BodyParser.parseProcedures(process.getNavObjects(), progressBar);

                    progressBar.setVisible(false);
                    progressBar.dispose();
                    //process.printObjects();

                    mainFrame.updateTree(process.getNavObjects());


                } else {
                    String fileNotExists = String.format("File %s not exists", path);
                    System.out.println(fileNotExists);
                    JOptionPane.showMessageDialog(mainFrame, fileNotExists);
                }
            }
        }
    }
}
