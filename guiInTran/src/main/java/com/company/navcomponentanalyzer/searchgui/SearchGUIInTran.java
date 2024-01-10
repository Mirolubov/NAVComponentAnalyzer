package com.company.navcomponentanalyzer.searchgui;

import com.company.navcomponentanalyzer.core.model.NavObjects;
import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;

public class SearchGUIInTran implements SearchProcessor {
    private NavObjects navObjects;
    private final String MODULE_NAME = "UI in transaction";
    private final String CONSOLE_ARGUMENT = "-gui";

    public void setNavObjects(NavObjects navObjects) {
        this.navObjects = navObjects;
    }

    @Override
    public Object[][] search(String searchStr) {
        return new Object[0][];
    }

    @Override
    public String getCaption() {
        return MODULE_NAME;
    }

    @Override
    public String getConsoleArgument() {
        return CONSOLE_ARGUMENT;
    }
}
