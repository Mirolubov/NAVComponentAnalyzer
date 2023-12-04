package com.company.model.search;

import com.company.model.*;
import com.company.view.MainFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchCaptionMLMiss implements SearchProcessor{
    private final NavObjects navObjects;
    private final MainFrame mainFrame;

    public SearchCaptionMLMiss(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.navObjects = mainFrame.getNavObjects();
    }

    @Override
    public Object[][] search(String searchStr) {
        List<SearchResult> searchResultList = new ArrayList<>();

        for (NavObject n : navObjects.getNavObjectsList()) {
            if (n.getNavType() == NavType.Table && n instanceof Table) {
                Map<String, Field> fields = ((Table) n).getFields();
                for (Map.Entry<String, Field> fieldMap : fields.entrySet()) {
                    Field field = fieldMap.getValue();
                    Map<String, String> captions = field.getCaptions();
                    if (!captions.containsKey("RUS") || !captions.containsKey("ENU")) {
                        SearchResult result = new SearchResult();
                        result.setType(n.getNavType().toString());
                        result.setName(n.getName());
                        result.setNo(String.valueOf(n.getId()));
                        result.setLine(String.valueOf(field.getLineNo()));
                        result.setText(field.getCaption().stripLeading());
                        searchResultList.add(result);
                    }

                }
            }
        }
        return SearchProcessor.getData(searchResultList);
    }

    @Override
    public MainFrame getMainFrame() {
        return mainFrame;
    }
}
