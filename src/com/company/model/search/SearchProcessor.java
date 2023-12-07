package com.company.model.search;

import com.company.view.MainFrame;

import java.util.List;

public interface SearchProcessor {
    Object[][] search(String searchStr);

    static Object[][] getData(List<SearchResult> searchResultList) {
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
}
