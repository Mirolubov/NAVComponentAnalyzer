package com.company.model.search;

import com.company.model.NavObject;
import com.company.model.NavObjects;
import com.company.view.MainFrame;

import java.util.ArrayList;
import java.util.List;

public class SearchText implements SearchProcessor{
    private final NavObjects navObjects;

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
}
