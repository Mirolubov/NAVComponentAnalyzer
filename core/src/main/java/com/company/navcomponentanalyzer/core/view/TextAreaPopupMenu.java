package com.company.navcomponentanalyzer.core.view;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;

public class TextAreaPopupMenu extends JPopupMenu {
    public TextAreaPopupMenu() {
        JMenuItem copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyItem.setText("Copy");
        add(copyItem);
    }
}
