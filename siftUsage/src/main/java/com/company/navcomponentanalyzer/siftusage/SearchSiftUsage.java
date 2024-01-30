package com.company.navcomponentanalyzer.siftusage;

import com.company.navcomponentanalyzer.core.model.*;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.object.NavType;
import com.company.navcomponentanalyzer.core.model.object.Table;
import com.company.navcomponentanalyzer.core.model.object.element.*;
import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.model.search.SearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SearchSiftUsage implements SearchProcessor {
    private NavObjects navObjects;
    private final String MODULE_NAME = "Unused SIFT";
    private final String CONSOLE_ARGUMENT = "-sift";
    private final String CALCSUMS = "CALCSUMS";
    private final String SETCURRENTKEY = "SETCURRENTKEY";

    @Override
    public Object[][] search(String searchStr) {
        List<SearchResult> searchResultList = new ArrayList<>();
        boolean siftUsed = false;
        for (NavObject n : navObjects.getNavObjectsList()) {
            if (n.isTable() && n instanceof Table) {
                List<Index> keys = ((Table) n).getKeys();
                for (Index key : keys) {
                    if(key.isSift()){
                        siftUsed = searchSiftInNavObjects(key);
                        if(!siftUsed){
                            SearchResult result = new SearchResult();
                            result.setType(n.getNavType().toString());
                            result.setName(n.getName());
                            result.setNo(String.valueOf(n.getId()));
                            result.setLine(String.valueOf(key.getLineNo()));
                            result.setText(((String) (n.getBody().lines().toArray())[key.getLineNo() - 1]).stripLeading());
                            searchResultList.add(result);
                        }
                    }
                }
            }
        }
        return SearchResult.getData(searchResultList);
    }

    private boolean searchSiftInNavObjects(Index key) {
        boolean siftUsed = false;
        Set<NavObject> navObjectsList = navObjects.getNavObjectsList();
        for (NavObject navObject : navObjectsList) {
            siftUsed = searchSiftInObject(navObject, key);
            if(siftUsed) {
                break;
            }
        }
        return siftUsed;
    }

    private boolean searchSiftInObject(NavObject navObject, Index key) {
        boolean siftUsed = false;
        if(navObject.getId() == key.getTableNo() && navObject.isTable()) {
            //поиск в той же таблице
            String body = navObject.getBody();
            String lastPart = body;
            int calcsumPos = 0, setcurrentkeyPos = 0;
            while((setcurrentkeyPos = lastPart.indexOf(" " + SETCURRENTKEY))!=-1){
                lastPart = lastPart.substring(setcurrentkeyPos + SETCURRENTKEY.length() + 2); //2 - 2 braces ()
                int bracePos = lastPart.indexOf(")");
                String keyFields = lastPart.substring(0, bracePos);
                keyFields = keyFields.replace("\"", "");
                String[] splitFields = keyFields.split(",");
                if(!Arrays.asList(splitFields).containsAll(key.getSiftFields())) {
                    continue;
                }
                if((calcsumPos = lastPart.indexOf(" " + CALCSUMS))!=-1){
                    String lastPartCalc = lastPart.substring(calcsumPos + CALCSUMS.length() + 2); //2 - 2 braces ()
                    int bracePosCalc = lastPartCalc.indexOf(")");
                    String calcFields = lastPart.substring(0, bracePos);
                    calcFields = calcFields.replace("\"", "");
                    String[] splitFieldsCalc = calcFields.split(",");
                    if(key.getSiftFields().containsAll(Arrays.asList(splitFieldsCalc))){
                        return true;
                    }
                }

            }
        }
        //среди calcformula
        if(navObject.isTable()) {
            Table table = (Table)navObject;
            Map<String, Field> fields = table.getFields();
            for (Map.Entry<String, Field> fieldMap : fields.entrySet()) {
                Field field = fieldMap.getValue();
                if(field.isFlowfield()){
                    CalcFormula calcFormula = field.getCalcFormula();
                    if(calcFormula.getAgregate() == null) {
                        System.out.println("Proble CalcFormula:");
                        System.out.println(calcFormula.getBody());
                    }
                    if (calcFormula.getAgregate().equals("Sum")) {
                        if(!key.getTableName().equals(calcFormula.getTableName())){
                            continue;
                        }
                        if(!key.getSiftFields().contains(calcFormula.getFieldName())){
                            continue;
                        }
                        if(calcFormula.getColumns().containsAll(key.getFields())){
                            return true;
                        }
                    }
                }
            }
        }
        //среди глобальных
        Map<String, Var> varList = navObject.getVarList();
        siftUsed = searchUsesInVarList(key, navObject, varList);
        if(siftUsed) {
            return true;
        }
        //среди локальных
        Map<String, Procedure> procedures = navObject.getProcedures();
        for (Map.Entry<String, Procedure> proc : procedures.entrySet()) {
            Procedure procedure = proc.getValue();
            Map<String, Var> procVarList = procedure.getVarList();
            siftUsed = searchUsesInVarList(key, navObject, procVarList);
            if (siftUsed) {
                return true;
            }
        }
        return siftUsed;
    }

    private boolean searchUsesInVarList(Index key, NavObject navObject, Map<String, Var> varList) {
        for(Map.Entry<String, Var> var: varList.entrySet()){
            Var variable = var.getValue();
            if(variable.getObjectId().intValue() == key.getTableNo() && variable.getType().equals(NavType.Table.toString())) {
                Map<String, List<Integer>> executes = variable.getExecutes();
                if (executes.containsKey(CALCSUMS)) {
                    List<Integer> linesExecute = executes.get(CALCSUMS);
                    for(Integer line: linesExecute) {
                        String[] splitFields = null;
                        try {
                            splitFields = extractFieldsFrom(CALCSUMS, navObject, line);
                        }catch (IndexOutOfBoundsException e) {
                            System.out.println("Error: " + e.getLocalizedMessage());
                            System.out.println(this.getClass().toString() +  ": searchUsesInVarList.extractFieldsFrom(CALCSUMS)");
                            System.out.println(((String) (navObject.getBody().lines().toArray())[line - 1]).stripLeading());
                        }
                        if(key.getSiftFields().containsAll(Arrays.asList(splitFields))) {
                            //Мы нашли CALCSUMS, теперь надо убедится что перед ним стоит SETCURRENTKEY с нужными полями
                            if (executes.containsKey(SETCURRENTKEY)) {
                                List<Integer> linesExecuteSCC = executes.get(SETCURRENTKEY);
                                for (Integer lineSCC : linesExecuteSCC) {
                                    if(lineSCC <= line) {
                                        try {
                                            String[] splitSCCFields = extractFieldsFrom(SETCURRENTKEY, navObject, lineSCC);
                                            if(Arrays.asList(splitSCCFields).containsAll(key.getFields())){
                                                return true;
                                            }
                                        }catch (IndexOutOfBoundsException e) {
                                            System.out.println("Error: " + e.getLocalizedMessage());
                                            System.out.println(this.getClass().toString() +  ": searchUsesInVarList.extractFieldsFrom(SETCURRENTKEY)");
                                            System.out.println(((String) (navObject.getBody().lines().toArray())[lineSCC - 2]).stripLeading());
                                            System.out.println(((String) (navObject.getBody().lines().toArray())[lineSCC - 1]).stripLeading());
                                            System.out.println(((String) (navObject.getBody().lines().toArray())[lineSCC - 0]).stripLeading());
                                            System.out.println(((String) (navObject.getBody().lines().toArray())[lineSCC + 1]).stripLeading());
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return false;
    }

    private String[] extractFieldsFrom(String methodName, NavObject navObject, int line) {
        String calcLine = ((String) (navObject.getBody().lines().toArray())[line - 1]).stripLeading();
        if(calcLine.isBlank()) {
            calcLine = ((String) (navObject.getBody().lines().toArray())[++line]).stripLeading();
        }
        String calcFields = calcLine.substring(calcLine.indexOf(methodName) + methodName.length()).stripLeading();
        int pos = calcFields.indexOf(")");
        while (pos == -1) {
            calcFields += ((String) (navObject.getBody().lines().toArray())[++line - 1]).stripLeading();
            pos = calcFields.indexOf(")");
        }
        calcFields = calcFields.substring(1, pos);
        calcFields = calcFields.replace("\"", "");
        return calcFields.split(",");
    }

    @Override
    public String getCaption() {
        return MODULE_NAME;
    }

    @Override
    public String getConsoleArgument() {
        return CONSOLE_ARGUMENT;
    }

    @Override
    public void setNavObjects(NavObjects navObjects) {
        this.navObjects = navObjects;
    }

    @Override
    public String getDescription() {
        try (InputStream in = getClass().getModule().getResourceAsStream("help.txt")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
