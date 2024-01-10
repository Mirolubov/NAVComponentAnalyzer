package com.company.navcomponentanalyzer.core.listener;

import com.company.navcomponentanalyzer.core.model.Table;
import com.company.navcomponentanalyzer.core.view.MainFrame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;

public class HighlightedTextAreaListener implements DocumentListener {
    private final StyledDocument doc;
    private final Style style;
    private final MainFrame mainFrame;
    private final DefaultListModel<Integer> lineNumberListModel;

    public HighlightedTextAreaListener(MainFrame mainFrame, StyledDocument doc, DefaultListModel<Integer> lineNumberListModel) {
        this.mainFrame = mainFrame;
        this.doc = doc;
        this.lineNumberListModel = lineNumberListModel;
        // Add a style for the keywords
        this.style = doc.addStyle("BlueColor", null);
        StyleConstants.setForeground(style, Color.BLUE);

    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        if(mainFrame.getNavObjects() == null)
            return;
        if(mainFrame.getNavObjects().getSelectedObject() == null)
            return;
        if (mainFrame.getNavObjects().getSelectedObject().getLineCount() ==
                documentEvent.getDocument().getDefaultRootElement().getElementCount()) {
            //System.out.println("insertUpdate");
            highlightKeywords();
            updateLineNumbers(documentEvent.getDocument());
        }
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
    }

    private void highlightKeywords() {
        Runnable doHighlight = new Runnable() {
            @Override
            public void run() {
                //System.out.println("highlightKeywords");
                // Remove any existing highlighting
                doc.setCharacterAttributes(0, doc.getLength(), doc.getStyle("default"), true);

                // Apply new highlighting to keywords
                for (String keyword : Table.SYSTEM_PROC) {
                    highlightWord(doc, keyword, style);
                }
            }
        };
        SwingUtilities.invokeLater(doHighlight);

    }

    private void updateLineNumbers(Document document) {
        Runnable doUpdateLineNumbers = new Runnable() {
            @Override
            public void run() {
                Element root = document.getDefaultRootElement();
                int totalLines = root.getElementCount();
                lineNumberListModel.clear();
                for (int i = 1; i <= totalLines; i++) {
                    lineNumberListModel.addElement(i);
                }
            }
        };
        SwingUtilities.invokeLater(doUpdateLineNumbers);
    }

    private static void highlightWord(StyledDocument doc, String word, Style style) {
        String text = null;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        int pos = 0;
        if (text != null) {
            while ((pos = text.indexOf(word, pos)) >= 0) {
                doc.setCharacterAttributes(pos, word.length(), style, false);
                pos += word.length();
            }
        }
    }

}
