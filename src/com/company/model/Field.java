package com.company.model;

import java.util.ArrayList;

public class Field {
    private final String name;
    private final int lineNo;
    private final ArrayList<Trigger> triggers;

    public Field(String name, int lineNo) {
        this.name = name;
        this.lineNo = lineNo;
        triggers = new ArrayList<>();
    }

    public ArrayList<Trigger> getTriggers() {
        return triggers;
    }

}
