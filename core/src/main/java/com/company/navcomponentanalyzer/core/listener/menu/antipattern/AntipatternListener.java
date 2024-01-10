package com.company.navcomponentanalyzer.core.listener.menu.antipattern;

import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.view.MainFrame;
import com.company.navcomponentanalyzer.core.view.SearchResultFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AntipatternListener implements ActionListener {
    private final MainFrame mainFrame;
    private final SearchProcessor searchProcessor;

    public AntipatternListener(MainFrame mainFrame, SearchProcessor searchProcessor) {
        this.mainFrame = mainFrame;
        this.searchProcessor = searchProcessor;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object[][] searchResult = searchProcessor.search("");
        SearchResultFrame newFrame = new SearchResultFrame("", searchResult, mainFrame);
        newFrame.setLocationRelativeTo(mainFrame);
        newFrame.setVisible(true);
    }
}

