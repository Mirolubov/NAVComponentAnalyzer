package com.company.navcomponentanalyzer.core.listener.menu.antipattern;

import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.model.search.SearchTransactionInValidate;
import com.company.navcomponentanalyzer.core.view.MainFrame;
import com.company.navcomponentanalyzer.core.view.SearchResultFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransactionInValidateListener implements ActionListener {
    private final MainFrame mainFrame;

    public TransactionInValidateListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        SearchProcessor searchProcessor = new SearchTransactionInValidate(mainFrame.getNavObjects());
        Object[][] searchResult = searchProcessor.search("");
        SearchResultFrame newFrame = new SearchResultFrame("", searchResult, mainFrame);
        newFrame.setLocationRelativeTo(mainFrame);
        newFrame.setVisible(true);
    }
}
