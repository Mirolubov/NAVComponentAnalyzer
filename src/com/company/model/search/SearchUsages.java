package com.company.model.search;

import com.company.model.*;
import com.company.view.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchUsages implements SearchProcessor{
    private final NavObjects navObjects;
    private final MainFrame mainFrame;

    public SearchUsages(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.navObjects = mainFrame.getNavObjects();
    }

    @Override
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    @Override
    public Object[][] search(String searchStr) {
        if (navObjects == null) {
            return null;
        }
        List<SearchResult> searchResultList = new ArrayList<>();
        // Поиск в объекте
        NavObject selectedObject = navObjects.getSelectedObject();
        List<Integer> lines = BodyParser.getNameUsesInBody(selectedObject.getBody(), searchStr);
        for(Integer line: lines) {
            SearchResult result = new SearchResult();
            result.setType(selectedObject.getNavType().toString());
            result.setName(selectedObject.getName());
            result.setNo(String.valueOf(selectedObject.getId()));
            result.setLine(String.valueOf(line + 1));
            result.setText(((String) (selectedObject.getBody().lines().toArray())[line]).stripLeading());
            searchResultList.add(result);
        }
        // Поиск по объектам
        Set<NavObject> navObjectsList = navObjects.getNavObjectsList();
        for (NavObject navObject : navObjectsList) {
            //среди глобальных
            Map<String, Var> varList = navObject.getVarList();
            searchUsesInVarList(searchStr, navObject, varList, selectedObject, searchResultList);
            //среди локальных
            Map<String, Procedure> procedures = navObject.getProcedures();
            for (Map.Entry<String, Procedure> proc : procedures.entrySet()) {
                Procedure procedure = proc.getValue();
                Map<String, Var> procVarList = procedure.getVarList();
                searchUsesInVarList(searchStr, navObject, procVarList, selectedObject, searchResultList);
            }
        }
        return SearchProcessor.getData(searchResultList);
    }

    private void searchUsesInVarList(String searchStr, NavObject navObject, Map<String, Var> varList, NavObject selectedObject, List<SearchResult> searchResultList) {
        for(Map.Entry<String, Var> var: varList.entrySet()){
            Var variable = var.getValue();
            if(variable.getObjectId() == selectedObject.getId() && variable.getType().equals(selectedObject.getNavType().toString())) {
                Map<String, List<Integer>> executes = variable.getExecutes();
                if (executes.containsKey(searchStr)) {
                    List<Integer> linesExecute = executes.get(searchStr);
                    for(Integer line: linesExecute) {
                        SearchResult result = new SearchResult();
                        result.setType(navObject.getNavType().toString());
                        result.setName(navObject.getName());
                        result.setNo(String.valueOf(navObject.getId()));
                        result.setLine(String.valueOf(line));
                        result.setText(((String) (navObject.getBody().lines().toArray())[line - 1]).stripLeading());
                        searchResultList.add(result);
                    }
                }
            }
        }
    }
}
