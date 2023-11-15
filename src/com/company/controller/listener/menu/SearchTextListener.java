package com.company.controller.listener.menu;

import com.company.view.MainFrame;
import com.company.controller.listener.SearchFieldActionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchTextListener implements ActionListener {
    private final MainFrame mainFrame;

    public SearchTextListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFrame frame = new JFrame("Search text");

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 25));
        SearchFieldActionListener searchFieldActionListener = new SearchFieldActionListener(mainFrame, searchField);
        searchField.addActionListener(searchFieldActionListener);

        JButton button = new JButton();
        button.setText("Search...");
        button.addActionListener(searchFieldActionListener);

        JPanel panelSearch = new JPanel(new BorderLayout());
        panelSearch.add(searchField, BorderLayout.CENTER);
        panelSearch.add(button, BorderLayout.EAST);

        frame.getContentPane().add(panelSearch);
        frame.setSize(320, 60);

        frame.pack();
        frame.setLocationRelativeTo(mainFrame);
        frame.setVisible(true);


    }
}
