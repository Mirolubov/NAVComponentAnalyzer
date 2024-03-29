package com.company.navcomponentanalyzer.core.listener.menu;

import com.company.navcomponentanalyzer.core.model.process.FileLoader;
import com.company.navcomponentanalyzer.core.view.MainFrame;

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
                openFile(false);
                break;
            case "Append":
                openFile(true);
                break;
            default:
                FileLoader fileLoader = new FileLoader(mainFrame);
                fileLoader.setCommand(command);
                fileLoader.clearNavObjects();
                fileLoader.processFile();
                break;
        }
    }

    private void openFile(boolean append) {
        JFileChooser fileOpen = new JFileChooser();
        int ret = fileOpen.showDialog(null, "Open File");
        if (ret == JFileChooser.APPROVE_OPTION) {
            String path = fileOpen.getSelectedFile().getAbsolutePath();
            FileLoader fileLoader = new FileLoader(path, mainFrame, mainFrame.getNavObjects());
            if (!append) {
                fileLoader.clearNavObjects();
            }
            fileLoader.processFile();
        }
    }


}
