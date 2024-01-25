package com.company.navcomponentanalyzer.core.listener;

import com.company.navcomponentanalyzer.core.model.object.Form;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.object.Table;
import com.company.navcomponentanalyzer.core.model.object.element.*;
import com.company.navcomponentanalyzer.core.view.MainFrame;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Map;

public class ObjectTreeSelectionListener implements TreeSelectionListener {
    private final MainFrame mainFrame;

    public ObjectTreeSelectionListener(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        JTextPane textArea = mainFrame.getTextArea();
        DefaultListModel<String> listModel = mainFrame.getListModel();
        JTree tree = mainFrame.getTree();

        textArea.selectAll();
        textArea.replaceSelection("");
        listModel.clear();
        TreePath selectedPath = tree.getSelectionPath();
        if (selectedPath != null) {
            Object selectedNode = selectedPath.getLastPathComponent();
            if (selectedNode instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedNode;
                Object userObject = treeNode.getUserObject();
                if (userObject instanceof NavObject) {
                    NavObject selectedObject = (NavObject) treeNode.getUserObject();
                    mainFrame.getNavObjects().setSelectedObject(selectedObject);

                    textArea.setText(selectedObject.getBody());
                    listModel.addElement("Variables");
                    Map<String, Var> varList = selectedObject.getVarList();
                    for (Map.Entry<String, Var> variable : varList.entrySet()) {
                        listModel.addElement(variable.getKey());
                    }
                    listModel.addElement("Triggers");
                    Map<String, Procedure> procedures = selectedObject.getProcedures();
                    for (Map.Entry<String, Procedure> procMap : procedures.entrySet()) {
                        Procedure procedure = procMap.getValue();
                        if (procedure instanceof Trigger) {
                            listModel.addElement(procedure.getName());
                        }
                    }
                    listModel.addElement("Procedures");
                    for (Map.Entry<String, Procedure> procMap : procedures.entrySet()) {
                        Procedure procedure = procMap.getValue();
                        if (!(procedure instanceof Trigger)) {
                            listModel.addElement(procedure.getName());
                        }
                    }
                    if (selectedObject.isTable()) {
                        listModel.addElement("Fields");
                        if (selectedObject instanceof Table) {
                            Table table = (Table)selectedObject;
                            Map<String, Field> fields = table.getFields();
                            for (Map.Entry<String, Field> fieldMap : fields.entrySet()) {
                                listModel.addElement(fieldMap.getKey());
                                Field field = fieldMap.getValue();
                                for (Trigger trigger : field.getTriggers()) {
                                    listModel.addElement("  tr: " + trigger.getName());
                                }
                            }
                        }

                    }
                    if (selectedObject.isForm()) {
                        listModel.addElement("Controls");
                        if (selectedObject instanceof Form) {
                            Form form = (Form)selectedObject;
                            Map<String, Control> controls = form.getControls();
                            for (Map.Entry<String, Control> controlMap : controls.entrySet()) {
                                listModel.addElement(controlMap.getKey());
                                Control control = controlMap.getValue();
                                for (Trigger trigger : control.getTriggers()) {
                                    listModel.addElement("  tr: " + trigger.getName());
                                }
                            }
                        }
                    }
                } else {
                    textArea.setText(treeNode.toString());
                }
                mainFrame.repaint();
            }
        }
    }
}
