package com.company.navcomponentanalyzer.core.listener.menu;

import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.model.search.SearchUsages;
import com.company.navcomponentanalyzer.core.view.MainFrame;
import com.company.navcomponentanalyzer.core.view.SearchResultFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindUsageListener implements ActionListener {
    private final MainFrame mainFrame;

    public FindUsageListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(mainFrame.getNavObjects().getSelectedObject() == null){
            return;
        }
        JList<String> list = mainFrame.getList();
        String selectedString = list.getSelectedValue();
        if (selectedString == null){
            return;
        }
        SearchProcessor searchProcessor = new SearchUsages(mainFrame.getNavObjects());
        Object[][] searchResult = searchProcessor.search(selectedString);
        SearchResultFrame newFrame = new SearchResultFrame(selectedString, searchResult, mainFrame);
        newFrame.setLocationRelativeTo(mainFrame);
        newFrame.setVisible(true);
    }
}
