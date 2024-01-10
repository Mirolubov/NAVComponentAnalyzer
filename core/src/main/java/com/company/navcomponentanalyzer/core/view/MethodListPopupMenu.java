package com.company.navcomponentanalyzer.core.view;

import com.company.navcomponentanalyzer.core.listener.menu.FindUsageListener;

import javax.swing.*;

public class MethodListPopupMenu extends JPopupMenu {

    public MethodListPopupMenu(MainFrame mainFrame) {
        JMenuItem copyItem = new JMenuItem("Find usage");
        FindUsageListener findUsageListener = new FindUsageListener(mainFrame);
        copyItem.addActionListener(findUsageListener);
        add(copyItem);
    }
}
