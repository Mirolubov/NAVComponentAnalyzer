package com.company.navcomponentanalyzer.core.listener;

import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.model.search.SearchText;
import com.company.navcomponentanalyzer.core.view.MainFrame;
import com.company.navcomponentanalyzer.core.view.SearchResultFrame;

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
        SearchProcessor searchProcessor = new SearchText(mainFrame.getNavObjects());
        Object[][] searchResult = searchProcessor.search(searchField.getText());
        SearchResultFrame newFrame = new SearchResultFrame(searchField.getText(), searchResult, mainFrame);
        newFrame.setLocationRelativeTo(mainFrame);
        newFrame.setVisible(true);
    }
}

