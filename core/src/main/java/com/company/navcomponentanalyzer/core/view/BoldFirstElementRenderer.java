package com.company.navcomponentanalyzer.core.view;
import com.company.navcomponentanalyzer.core.model.Table;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class BoldFirstElementRenderer extends DefaultListCellRenderer {

    private final static String [] keyWords = {"Procedures", "Fields", "Variables"};

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Font originalFont = renderer.getFont();
        Font boldFont = new Font(originalFont.getFontName(), Font.BOLD, originalFont.getSize());

        List<String> keyList = Arrays.asList(keyWords);
        if (keyList.contains((String)value)) {
            renderer.setFont(boldFont);
        }

        keyList = Arrays.asList(Table.SYSTEM_PROC);
        if (keyList.contains((String)value)) {
            renderer.setForeground(Color.BLUE);
        }

        return renderer;
    }

}
