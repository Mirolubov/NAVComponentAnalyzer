//Copyright 2022 Artiom Mirolyubov
//Licensed under the Apache License, Version 2.0
package com.company.controller.listener.menu;

import com.company.config.ConfigFile;
import com.company.model.BodyParser;
import com.company.model.process.FileLoader;
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
                FileLoader fileLoader = new FileLoader(mainFrame);
                fileLoader.setCommand(command);
                fileLoader.clearNavObjects();
                fileLoader.processFile();
                break;
        }
    }

    private void openFile() {
        JFileChooser fileOpen = new JFileChooser();
        int ret = fileOpen.showDialog(null, "Open File");
        if (ret == JFileChooser.APPROVE_OPTION) {
            String path = fileOpen.getSelectedFile().getAbsolutePath();
            FileLoader fileLoader = new FileLoader(path, mainFrame, mainFrame.getNavObjects());
            fileLoader.clearNavObjects();
            fileLoader.processFile();
        }
    }


}
