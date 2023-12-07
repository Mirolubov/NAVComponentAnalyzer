package com.company.controller.listener.menu.antipattern;

import com.company.model.search.SearchProcessor;
import com.company.model.search.SearchTransactionInValidate;
import com.company.view.MainFrame;
import com.company.view.SearchResultFrame;

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
        SearchResultFrame newFrame = new SearchResultFrame("", searchProcessor, mainFrame);
        newFrame.setLocationRelativeTo(mainFrame);
        newFrame.setVisible(true);
    }
}
