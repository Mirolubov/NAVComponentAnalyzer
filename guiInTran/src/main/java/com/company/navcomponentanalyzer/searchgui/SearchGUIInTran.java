package com.company.navcomponentanalyzer.searchgui;

import com.company.navcomponentanalyzer.core.model.NavObjects;
import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    @Override
    public String getDescription() {
        try (InputStream in = getClass().getModule().getResourceAsStream("help.txt")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
