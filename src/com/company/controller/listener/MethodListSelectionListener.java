package com.company.controller.listener;

import com.company.model.NavObject;
import com.company.model.Table;
import com.company.view.MainFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class MethodListSelectionListener implements ListSelectionListener {
    private final MainFrame mainFrame;

    public MethodListSelectionListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getValueIsAdjusting())
            return;

        JTextPane textArea =  mainFrame.getTextArea();
        JList<String> list = mainFrame.getList();
        NavObject selectedObject = mainFrame.getNavObjects().getSelectedObject();
        if(selectedObject == null){
            return;
        }
        Highlighter highlighter = textArea.getHighlighter();
        highlighter.removeAllHighlights();
        // Get the selected item(s)
        String selectedItem = list.getSelectedValue();
        if(selectedItem == null){
            return;
        }
        // Print the selected item(s)
        Map<String, Integer> procedureIndexes = selectedObject.getProcedureIndexes();
        Map<String, Integer> varIndexes = selectedObject.getVarIndexes();
        Integer lineNumber = procedureIndexes.get(selectedItem);
        if (lineNumber == null) {
            lineNumber = varIndexes.get(selectedItem);
            if (lineNumber == null) {
                if (selectedObject instanceof Table) {
                    Map<String, Integer> fieldIndexes = ((Table)selectedObject).getFieldIndexes();
                    lineNumber = fieldIndexes.get(selectedItem);
                }
            }
        }
        if(lineNumber != null) {
// Move the cursor to the specified line
            int position = textArea.getDocument().getDefaultRootElement().getElement(lineNumber - 1).getStartOffset();
// Select the entire line
            int lineEndOffset = textArea.getDocument().getDefaultRootElement().getElement(lineNumber - 1).getEndOffset();
            textArea.select(position, lineEndOffset);
// Highlight the selected line
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
        }

    }
}
