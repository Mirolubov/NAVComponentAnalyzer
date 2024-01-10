package com.company.navcomponentanalyzer.core.listener;

import com.company.navcomponentanalyzer.core.view.MainFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SearchTableSelectionListener implements ListSelectionListener {
    private final MainFrame mainFrame;
    private final JTable table;

    public SearchTableSelectionListener(MainFrame mainFrame, JTable table) {
        this.mainFrame = mainFrame;
        this.table = table;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getValueIsAdjusting())
            return;

        int selIndex = table.getSelectedRow();

        JTree tree = mainFrame.getTree();

        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode baseRoot = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode nodeType = null, nodeObject = null;
        for (int i = 0; i < baseRoot.getChildCount(); i++) {
            nodeType = (DefaultMutableTreeNode) baseRoot.getChildAt(i);
            if (nodeType.toString().equals(table.getValueAt(selIndex, 0))) {
                for (int j = 0; j < nodeType.getChildCount(); j++) {
                    nodeObject = (DefaultMutableTreeNode) nodeType.getChildAt(j);
                    if (nodeObject.toString().equals(String.format("%s (%s)", table.getValueAt(selIndex, 1), table.getValueAt(selIndex, 2)))) {
                        break;
                    }
                }
                break;
            }
        }
        TreePath path = new TreePath(new Object[]{baseRoot, nodeType, nodeObject});
        tree.setSelectionPath(path);

        JTextPane textArea = mainFrame.getTextArea();
        int lineNumber = Integer.parseInt((String)table.getValueAt(selIndex, 3));
// Move the cursor to the specified line
        if ((textArea.getDocument().getDefaultRootElement().getElement(lineNumber - 1)) == null)
            return;
        int position = textArea.getDocument().getDefaultRootElement().getElement(lineNumber - 1).getStartOffset();
// Select the entire line
        int lineEndOffset = textArea.getDocument().getDefaultRootElement().getElement(lineNumber - 1).getEndOffset();
        textArea.select(position, lineEndOffset);
// Highlight the selected line
        Highlighter highlighter = textArea.getHighlighter();
        highlighter.removeAllHighlights();

        DefaultHighlighter.DefaultHighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        try {
            highlighter.addHighlight(position, lineEndOffset, painter);
            // Scroll to position
            Rectangle2D rect = textArea.modelToView2D(position);
            if (rect != null) {
                textArea.scrollRectToVisible(rect.getBounds());
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        mainFrame.repaint();
    }
}
