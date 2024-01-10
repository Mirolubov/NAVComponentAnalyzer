package search;

import com.company.navcomponentanalyzer.core.model.*;
import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.model.search.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchTransactionInValidate implements SearchProcessor {
    private NavObjects navObjects;

    public SearchTransactionInValidate(NavObjects navObjects) {
        this.navObjects = navObjects;
    }

    public SearchTransactionInValidate() {
    }

    @Override
    public Object[][] search(String searchStr) {
        List<SearchResult> searchResultList = new ArrayList<>();

        navObjects.getNavObjectsList().forEach((n) -> {
            if (n.getNavType() == NavType.Table && n instanceof Table) {
                Map<String, Field> fields = ((Table)n).getFields();
                for(Map.Entry<String, Field> fieldMap: fields.entrySet()){
                    Field field = fieldMap.getValue();
                    ArrayList<Trigger> triggers = field.getTriggers();
                    for(Trigger trigger : triggers){
                        Map<String, Var> varList = trigger.getVarList();
                        recurseSearchSystemProc(n, trigger, varList, searchResultList, "", "");
                    }
                }
            }
        });
        return SearchProcessor.getData(searchResultList);
    }

    private void recurseSearchSystemProc(NavObject n, Procedure procedure, Map<String, Var> varList, List<SearchResult> searchResultList,
                                         String fromLine,
                                         String fromText) {
        int tranLine = containTransaction(procedure.getBody());
        if (tranLine > 0) {
            SearchResult result = new SearchResult();
            result.setType(n.getNavType().toString());
            result.setName(n.getName());
            result.setNo(String.valueOf(n.getId()));
            if (fromLine.isEmpty()) {
                result.setLine(String.valueOf(procedure.getLineNo() + tranLine));
                result.setText(((String) (n.getBody().lines().toArray()[procedure.getLineNo() + tranLine - 1]))
                        .stripLeading());
            } else {
                result.setLine(fromLine);
                result.setText(fromText);

            }
            searchResultList.add(result);
            return;
        }

        for(Map.Entry<String, Var> varMap: varList.entrySet()) {
            Var variable = varMap.getValue();
            if(variable.getTemporary())
                continue;
            if(!NavType.checkNavObject(variable.getType()))
                continue;
            Map<String, List<Integer>> executes = variable.getExecutes();
            for (String keyword : Table.SYSTEM_PROC) {
                if (executes.containsKey(keyword)) {
                    List<Integer> lines = variable.getExecutes().get(keyword);
                    for (Integer line : lines) {
                        SearchResult result = new SearchResult();
                        result.setType(n.getNavType().toString());
                        result.setName(n.getName());
                        result.setNo(String.valueOf(n.getId()));
                        if (fromLine.isEmpty()) {
                            result.setLine(String.valueOf(line));
                            result.setText(((String) (n.getBody().lines().toArray()[line - 1]))
                                    .stripLeading());
                        } else {
                            result.setLine(fromLine);
                            result.setText(fromText);

                        }
                        searchResultList.add(result);
                        return;
                    }
                }
            }
            NavObject searchObject = new NavObject(variable.getObjectId(), "", NavType.fromString(variable.getType()));
            NavObject varObject = navObjects.getNavObjectsList().stream().
                    filter(obj -> obj.equals(searchObject))
                    .findFirst()
                    .orElse(null);
            if (varObject == null) {
                continue;
            }
            Map<String, Procedure> procedures = varObject.getProcedures();
            if(procedures == null) {
                continue;
            }
            for(Map.Entry<String, List<Integer>> executesEntry: executes.entrySet()) {
                if(procedures.containsKey(executesEntry.getKey())){
                    Procedure newProcedure = procedures.get(executesEntry.getKey());
                    Map<String, Var> procVarList = newProcedure.getVarList();
                    String newFromLine = fromLine;
                    String newFromText = fromText;
                    if(newFromLine.isEmpty()) {
                        newFromLine=String.valueOf(executesEntry.getValue().get(0));
                        newFromText=((String) (n.getBody().lines().toArray()[executesEntry.getValue().get(0) - 1]))
                                .stripLeading();
                    }
                    recurseSearchSystemProc(n, newProcedure, procVarList, searchResultList, newFromLine, newFromText);
                }

            }
        }
    }

    public static int containTransaction(String body){
        if(body == null)
            return 0;
        if(body.isBlank())
            return 0;
        for(int i =0; i < Table.SYSTEM_PROC.length; i++){
            Object [] lines = body.lines().toArray();
            for(int lineNo = 0; lineNo < lines.length; lineNo++) {
                String line = (String)lines[lineNo];
                if (line.contains(String.format(" %s", Table.SYSTEM_PROC[i])) ||
                        line.contains(String.format("(%s", Table.SYSTEM_PROC[i]))) {
                    return lineNo;
                }
            }
        }
        return 0;
    }
}
