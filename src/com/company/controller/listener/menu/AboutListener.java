package com.company.controller.listener.menu;

import com.company.view.AboutDialog;
import com.company.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutListener implements ActionListener {
    private final MainFrame mainFrame;

    public AboutListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        SwingUtilities.invokeLater(() -> {
            AboutDialog dialog = new AboutDialog(mainFrame);
            dialog.setVisible(true);
        });
    }
}

