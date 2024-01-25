package com.company.navcomponentanalyzer.core.model.search;

import com.company.navcomponentanalyzer.core.model.*;
import com.company.navcomponentanalyzer.core.model.object.Form;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.object.Table;
import com.company.navcomponentanalyzer.core.model.object.element.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchUsages implements SearchProcessor{
    private NavObjects navObjects;
    private final static String procFinishChars = "() ;\r\n<>!=,.:";
    private final static String procStartChars = "() ;<>!=,";
    private final String MODULE_NAME = "Search Usages";

    public SearchUsages(NavObjects navObjects) {
        this.navObjects = navObjects;
    }

    @Override
    public Object[][] search(String searchStr) {
        if (navObjects == null) {
            return null;
        }
        List<SearchResult> searchResultList = new ArrayList<>();
        // Поиск в объекте
        NavObject selectedObject = navObjects.getSelectedObject();
        List<Integer> lines = getNameUsesInBody(selectedObject.getBody(), searchStr);
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
            //среди триггеров полей
            if (navObject.isTable()) {
                Table table = (Table) navObject;
                Map<String, Field> fields = table.getFields();
                for (Map.Entry<String, Field> fieldMap : fields.entrySet()) {
                    Field field = fieldMap.getValue();
                    for (Trigger trigger : field.getTriggers()) {
                        Map<String, Var> procVarList = trigger.getVarList();
                        searchUsesInVarList(searchStr, navObject, procVarList, selectedObject, searchResultList);
                    }
                }
            }
            //среди триггеров контролов
            if (navObject.isForm()) {
                Form form = (Form) navObject;
                Map<String, Control> controls = form.getControls();
                for (Map.Entry<String, Control> controlMap : controls.entrySet()) {
                    Control control = controlMap.getValue();
                    for (Trigger trigger : control.getTriggers()) {
                        Map<String, Var> procVarList = trigger.getVarList();
                        searchUsesInVarList(searchStr, navObject, procVarList, selectedObject, searchResultList);
                    }
                }
            }
        }
        return SearchResult.getData(searchResultList);
    }

    @Override
    public String getCaption() {
        return MODULE_NAME;
    }

    @Override
    public String getConsoleArgument() {
        return null;
    }

    @Override
    public void setNavObjects(NavObjects navObjects) {
        this.navObjects = navObjects;
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

    public static List<Integer> getNameUsesInBody(String body, String searchName) {
        int lineNo = 0;
        List<Integer> ret = new ArrayList<>();
        Object[] lines = body.lines().toArray();
        for (Object object : lines) {
            String line = (String) object;
            line = clearComment(line);
            searchInLine(searchName, line, ret, lineNo++);
        }
        return ret;
    }

    private static String clearComment(String line) {
        int comment  = line.indexOf("//");
        if (comment >= 0) {
            line = line.substring(0, comment);
        }
        return line;
    }

    private static void searchInLine(String searchName, String line, List<Integer> ret, int lineNo) {
        for (char ch: procStartChars.toCharArray()) {
            String searchStr = String.format("%c%s", ch, searchName);
            if (searchInLineText(searchName, line, ret, lineNo, searchStr)) {
                break;
            }
        }
    }

    private static boolean searchInLineText(String searchName, String line, List<Integer> ret, int lineNo, String text) {
        int posText = line.indexOf(text);
        if (posText >= 0) {
            String strEnd = line.substring(posText + text.length());
            if(strEnd.isBlank()) {
                ret.add(lineNo);
                return true;
            }

            if (procFinishChars.contains(strEnd.substring(0, 1))) {
                ret.add(lineNo);
                return true;
            } else {
                searchInLine(searchName, strEnd, ret, lineNo);
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }

}
