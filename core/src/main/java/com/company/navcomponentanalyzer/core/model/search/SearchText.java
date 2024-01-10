package com.company.navcomponentanalyzer.core.model.search;

import com.company.navcomponentanalyzer.core.model.NavObject;
import com.company.navcomponentanalyzer.core.model.NavObjects;
import com.company.navcomponentanalyzer.core.view.MainFrame;

import java.util.ArrayList;
import java.util.List;

public class SearchText implements SearchProcessor{
    private NavObjects navObjects;
    private final String MODULE_NAME = "Search Text";

    public SearchText(NavObjects navObjects) {
        this.navObjects = navObjects;
    }

    @Override
    public Object[][] search(String searchStr) {
        if (navObjects == null) {
            return null;
        }
        List<SearchResult> searchResultList = new ArrayList<>();
        for(NavObject navObject: navObjects.getNavObjectsList()) {
            String[] lines = navObject.getBody().split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains(searchStr)) {
                    SearchResult result = new SearchResult();
                    result.setType(navObject.getNavType().toString());
                    result.setName(navObject.getName());
                    result.setNo(String.valueOf(navObject.getId()));
                    result.setLine(String.valueOf(i + 1));
                    result.setText(lines[i].stripLeading());
                    searchResultList.add(result);
                }
            }
        }
        return SearchProcessor.getData(searchResultList);
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
}
