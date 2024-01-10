package com.company.navcomponentanalyzer.core.listener;

import com.company.navcomponentanalyzer.core.model.*;
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
                    listModel.addElement("Procedures");
                    Map<String, Integer> procedureIndexes = selectedObject.getProcedureIndexes();
                    for (Map.Entry<String, Integer> proc : procedureIndexes.entrySet()) {
                        listModel.addElement(proc.getKey());
                    }
                    if (selectedObject.getNavType() == NavType.Table) {
                        listModel.addElement("Fields");
                        if (selectedObject instanceof Table) {
                            Map<String, Integer> fieldIndexes = ((Table)selectedObject).getFieldIndexes();
                            for (Map.Entry<String, Integer> field : fieldIndexes.entrySet()) {
                                listModel.addElement(field.getKey());
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
