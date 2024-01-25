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
    String getDescription();
    void setNavObjects(NavObjects navObjects);

    static List<SearchProcessor> getSearchProcessors(ModuleLayer layer) {
        return ServiceLoader
                .load(layer, SearchProcessor.class)
                .stream()
                .map(Provider::get)
                .collect(Collectors.toList());
    }
}
