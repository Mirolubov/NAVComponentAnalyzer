package com.company.navcomponentanalyzer.core.model;
import java.util.*;

public class Table extends NavObject {
    private final Map<String, Integer> triggerIndexes;
    private final Map<String, Integer> fieldIndexes;
    private final Map<String, Field> fields;
    public static final String[] SYSTEM_PROC = {"INSERT", "MODIFY", "MODIFYALL", "DELETE", "DELETEALL"};
    private final List<Index> keys;

    public Table(int id, String name, NavType navType){
        super(id, name, navType);
        this.triggerIndexes = new TreeMap<>();
        this.fieldIndexes = new TreeMap<>();
        this.fields = new TreeMap<>();
        this.keys = new ArrayList<>();
        for (String s : SYSTEM_PROC) {
            Procedure proc = new Procedure(s);
            getProcedures().put(s, proc);
            getProcedureIndexes().put(s, 1);
        }
    }

    public Map<String, Integer> getFieldIndexes() {
        return fieldIndexes;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public List<Index> getKeys() {
        return keys;
    }

    public void addKey(Index key) {
        this.keys.add(key);
    }
}
