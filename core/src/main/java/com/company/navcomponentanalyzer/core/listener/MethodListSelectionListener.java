package com.company.navcomponentanalyzer.core.listener;

import com.company.navcomponentanalyzer.core.model.object.Form;
import com.company.navcomponentanalyzer.core.model.object.element.Control;
import com.company.navcomponentanalyzer.core.model.object.element.Field;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.object.Table;
import com.company.navcomponentanalyzer.core.model.object.element.Trigger;
import com.company.navcomponentanalyzer.core.view.MainFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

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
        int selectionIndex = list.getMaxSelectionIndex();
        String selectedItem = list.getSelectedValue();
        ListModel<String> model = list.getModel();
        if(selectedItem == null){
            return;
        }
        // Get selected item line

        Map<String, Integer> procedureIndexes = selectedObject.getProcedureIndexes();
        Map<String, Integer> varIndexes = selectedObject.getVarIndexes();
        Integer lineNumber = procedureIndexes.get(selectedItem);
        if (lineNumber == null) {
            lineNumber = varIndexes.get(selectedItem);
            if (lineNumber == null) {
                if (selectedObject.isTable() && selectedObject instanceof Table) {
                    Table table = ((Table)selectedObject);
                    Map<String, Field> fields = table.getFields();
                    if (selectedItem.indexOf(Table.TRIGGER) == 0) {
                        String triggerName = selectedItem.substring(Table.TRIGGER.length());
                        String fieldName;
                        while ((fieldName = model.getElementAt(--selectionIndex)).indexOf(Table.TRIGGER) == 0) {
                        }
                        ArrayList<Trigger> triggers = fields.get(fieldName).getTriggers();
                        for (Trigger trigger : triggers) {
                            if(trigger.getName().equals(triggerName)) {
                                lineNumber = trigger.getLineNo();
                            }
                        }
                    }else {
                        lineNumber = fields.get(selectedItem).getLineNo();
                    }
                }
                if (selectedObject.isForm() && selectedObject instanceof Form) {
                    Form form = ((Form)selectedObject);
                    Map<String, Control> controls = form.getControls();
                    if (selectedItem.indexOf(Form.TRIGGER) == 0) {
                        String triggerName = selectedItem.substring(Form.TRIGGER.length());
                        String controlName;
                        while ((controlName = model.getElementAt(--selectionIndex)).indexOf(Form.TRIGGER) == 0) {
                        }
                        ArrayList<Trigger> triggers = controls.get(controlName).getTriggers();
                        for (Trigger trigger : triggers) {
                            if(trigger.getName().equals(triggerName)) {
                                lineNumber = trigger.getLineNo();
                            }
                        }
                    }else {
                        lineNumber = controls.get(selectedItem).getLineNo();
                    }
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
            mainFrame.repaint();
        }

    }
}
