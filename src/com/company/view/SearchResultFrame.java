package com.company.view;

import com.company.model.search.SearchProcessor;
import com.company.controller.listener.SearchTableSelectionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchResultFrame extends JFrame {
    private final String searchString;

    public SearchResultFrame(String title, SearchProcessor searchProcessor) throws HeadlessException {
        super(title);
        this.searchString = title;
        prepareUI(searchProcessor);
    }

    private void prepareUI(SearchProcessor searchProcessor) {
        setSize(640, 480);

        // Create the list area
        String[] columnNames = { "Type", "Name", "No", "Line", "Text" };
        Object[][] data = searchProcessor.search(searchString);


        JTable table = getJTable(searchProcessor, data, columnNames);
        // Add the table to a scroll pane and then add it to the panel
        JScrollPane scrollPane = new JScrollPane(table);

        getContentPane().add(scrollPane);

    }

    private static JTable getJTable(SearchProcessor searchProcessor, Object[][] data, String[] columnNames) {
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        // Create the table
        JTable table = new JTable(tableModel);
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SearchTableSelectionListener searchTableSelectionListener = new SearchTableSelectionListener(searchProcessor.getMainFrame(), table);
        selectionModel.addListSelectionListener(searchTableSelectionListener);
        table.setComponentPopupMenu(new TextAreaPopupMenu());
        return table;
    }
}
