package com.company.navcomponentanalyzer.searchml;

import com.company.navcomponentanalyzer.core.config.AppProperties;
import com.company.navcomponentanalyzer.core.model.*;
import com.company.navcomponentanalyzer.core.model.object.Form;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.object.Table;
import com.company.navcomponentanalyzer.core.model.object.element.Control;
import com.company.navcomponentanalyzer.core.model.object.element.Field;
import com.company.navcomponentanalyzer.core.model.object.element.Procedure;
import com.company.navcomponentanalyzer.core.model.object.element.Var;
import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.model.search.SearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchCaptionMLMiss implements SearchProcessor {
    private NavObjects navObjects;
    private final String MODULE_NAME = "Check caption ML";
    private final String CONSOLE_ARGUMENT = "-captionml";

    public void setNavObjects(NavObjects navObjects) {
        this.navObjects = navObjects;
    }

    public SearchCaptionMLMiss() {
    }

    @Override
    public Object[][] search(String searchStr) {
        List<SearchResult> searchResultList = new ArrayList<>();

        for (NavObject n : navObjects.getNavObjectsList()) {
            for(Map.Entry<String, Var> varEntry: n.getVarList().entrySet()) {
                checkVarCaptions(n, varEntry, searchResultList);
            }
            for(Map.Entry<String, Procedure> procedureEntry: n.getProcedures().entrySet()){
                for(Map.Entry<String,Var> varEntry: procedureEntry.getValue().getVarList().entrySet()) {
                    checkVarCaptions(n, varEntry, searchResultList);
                }
            }

            if (n.isTable() && n instanceof Table) {
                Map<String, Field> fields = ((Table) n).getFields();
                for (Map.Entry<String, Field> fieldMap : fields.entrySet()) {
                    Field field = fieldMap.getValue();
                    checkCaptions(n, field.getCaptions(), field.getLineNo(), field.getCaption().stripLeading(), searchResultList);
                }
            }
            if (n.isForm() && n instanceof Form) {
                Map<String, Control> controls = ((Form) n).getControls();
                for (Map.Entry<String, Control> controlMap : controls.entrySet()) {
                    Control control = controlMap.getValue();
                    checkCaptions(n, control.getCaptions(), control.getLineNo(), control.getCaption().stripLeading(), searchResultList);
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
        return CONSOLE_ARGUMENT;
    }

    private static void checkVarCaptions(NavObject navObject, Map.Entry<String, Var> varEntry, List<SearchResult> searchResultList) {
        Var variable = varEntry.getValue();
        if (variable.getType().equals(Var.CONST_TYPE)){
            checkCaptions(navObject, variable.getCaptions(), variable.getLineNo(), variable.getName().stripLeading(), searchResultList);
        }
    }

    private static void checkCaptions(NavObject navObject, Map<String, String> captions, int lineNo, String text, List<SearchResult> searchResultList) {
        AppProperties properties = AppProperties.initAppProperties();
        String captionsList = properties.getCaptionML();
        for (String captionLang : captionsList.split("\\|")) {
            if (!captions.containsKey(captionLang)) {
                SearchResult result = new SearchResult();
                result.setType(navObject.getNavType().toString());
                result.setName(navObject.getName());
                result.setNo(String.valueOf(navObject.getId()));
                result.setLine(String.valueOf(lineNo));
                result.setText(text);
                searchResultList.add(result);
                return;
            }
        }
    }

    @Override
    public String getDescription() {
        try (InputStream in = getClass().getModule().getResourceAsStream("help.txt")){
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
