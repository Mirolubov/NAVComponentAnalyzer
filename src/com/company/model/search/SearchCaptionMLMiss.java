package com.company.model.search;

import com.company.model.*;
import com.company.view.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchCaptionMLMiss implements SearchProcessor{
    private final NavObjects navObjects;

    public SearchCaptionMLMiss(NavObjects navObjects) {
        this.navObjects = navObjects;
    }

    @Override
    public Object[][] search(String searchStr) {
        List<SearchResult> searchResultList = new ArrayList<>();

        for (NavObject n : navObjects.getNavObjectsList()) {
            for(Map.Entry<String,Var> varEntry: n.getVarList().entrySet()) {
                checkVarCaptions(n, varEntry, searchResultList);
            }
            for(Map.Entry<String, Procedure> procedureEntry: n.getProcedures().entrySet()){
                for(Map.Entry<String,Var> varEntry: procedureEntry.getValue().getVarList().entrySet()) {
                    checkVarCaptions(n, varEntry, searchResultList);
                }
            }

            if (n.getNavType() == NavType.Table && n instanceof Table) {
                Map<String, Field> fields = ((Table) n).getFields();
                for (Map.Entry<String, Field> fieldMap : fields.entrySet()) {
                    Field field = fieldMap.getValue();
                    checkCaptions(n, field.getCaptions(), field.getLineNo(), field.getCaption().stripLeading(), searchResultList);
                }
            }
        }
        return SearchProcessor.getData(searchResultList);
    }

    private static void checkVarCaptions(NavObject navObject, Map.Entry<String, Var> varEntry, List<SearchResult> searchResultList) {
        Var variable = varEntry.getValue();
        if (variable.getType().equals(Var.CONST_TYPE)){
            checkCaptions(navObject, variable.getCaptions(), variable.getLineNo(), variable.getName().stripLeading(), searchResultList);
        }
    }

    private static void checkCaptions(NavObject navObject, Map<String, String> captions, int lineNo, String text, List<SearchResult> searchResultList) {
        if (!captions.containsKey("RUS") || !captions.containsKey("ENU")) {
            SearchResult result = new SearchResult();
            result.setType(navObject.getNavType().toString());
            result.setName(navObject.getName());
            result.setNo(String.valueOf(navObject.getId()));
            result.setLine(String.valueOf(lineNo));
            result.setText(text);
            searchResultList.add(result);
        }
    }
}
