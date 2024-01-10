package com.company.navcomponentanalyzer.core.listener.menu.antipattern;

import com.company.navcomponentanalyzer.core.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIInTransactionListener implements ActionListener {
    private final MainFrame mainFrame;

    public UIInTransactionListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JOptionPane.showMessageDialog(
                mainFrame.getComponent(0),
            "Not ready Yet!"
        );
    }
}
