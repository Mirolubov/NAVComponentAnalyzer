package com.company.navcomponentanalyzer.core.view;

import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.listener.SearchTableSelectionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchResultFrame extends JFrame {
    private final MainFrame mainFrame;
    private final Object[][] searchResult;
    private final String[] columnNames = { "Type", "Name", "No", "Line", "Text" };

    public SearchResultFrame(String title, Object[][] searchResult, MainFrame mainFrame) throws HeadlessException {
        super(title);
        this.mainFrame = mainFrame;
        this.searchResult = searchResult;
        prepareUI();
    }

    private void prepareUI() {
        setSize(640, 480);

        // Create the list area
        JTable table = getJTable(searchResult, columnNames);
        // Add the table to a scroll pane and then add it to the panel
        JScrollPane scrollPane = new JScrollPane(table);

        getContentPane().add(scrollPane);

    }

    private JTable getJTable(Object[][] data, String[] columnNames) {
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        // Create the table
        JTable table = new JTable(tableModel);
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        SearchTableSelectionListener searchTableSelectionListener = new SearchTableSelectionListener(mainFrame, table);
        selectionModel.addListSelectionListener(searchTableSelectionListener);
        table.setComponentPopupMenu(new TextAreaPopupMenu());
        return table;
    }
}
