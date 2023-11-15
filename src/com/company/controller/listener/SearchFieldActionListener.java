package com.company.controller.listener;

import com.company.model.search.SearchProcessor;
import com.company.model.search.SearchText;
import com.company.view.MainFrame;
import com.company.view.SearchResultFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchFieldActionListener implements ActionListener {
    private final MainFrame mainFrame;
    private final JTextField searchField;

    public SearchFieldActionListener(MainFrame mainFrame, JTextField searchField) {
        this.mainFrame = mainFrame;
        this.searchField = searchField;
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        SearchProcessor searchProcessor = new SearchText(mainFrame);
        SearchResultFrame newFrame = new SearchResultFrame(searchField.getText(), searchProcessor);
        newFrame.setLocationRelativeTo(mainFrame);
        newFrame.setVisible(true);
    }
}

