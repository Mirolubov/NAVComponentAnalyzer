package com.company.navcomponentanalyzer.core.model.search;

import com.company.navcomponentanalyzer.core.model.NavObjects;

import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

public interface SearchProcessor {
    Object[][] search(String searchStr);
    String getCaption();
    String getConsoleArgument();
    void setNavObjects(NavObjects navObjects);

    static  Object[][] getData(List<SearchResult> searchResultList) {
        Object[][] data = new Object[searchResultList.size()][5];
        int i = 0;
        for(SearchResult result: searchResultList){
            data[i][0] = result.getType();
            data[i][1] = result.getName();
            data[i][2] = result.getNo();
            data[i][3] = result.getLine();
            data[i][4] = result.getText();
            i++;
        }
        return data;
    }

    static List<SearchProcessor> getSearchProcessors(ModuleLayer layer) {
        return ServiceLoader
                .load(layer, SearchProcessor.class)
                .stream()
                .map(Provider::get)
                .collect(Collectors.toList());
    }
}
