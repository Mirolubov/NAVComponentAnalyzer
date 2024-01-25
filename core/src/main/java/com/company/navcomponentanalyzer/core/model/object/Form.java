package com.company.navcomponentanalyzer.core.model.object;

import com.company.navcomponentanalyzer.core.model.object.element.Control;

import java.util.Map;
import java.util.TreeMap;

public class Form extends NavObject{
    private final Map<String, Control> controls;
    public static final String TRIGGER = "  tr: ";

    public Form(int id, String name, NavType navType){
        super(id, name, navType);
        this.controls = new TreeMap<>();
    }

    public Map<String, Control> getControls() {
        return controls;
    }
}
