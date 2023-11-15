package com.company.model;
import java.util.*;

public class Table extends NavObject {
    private final Map<String, Integer> triggerIndexes;
    private final Map<String, Integer> fieldIndexes;
    private final Map<String, Field> fields;
    public static final String[] SYSTEM_PROC = {"INSERT", "MODIFY", "MODIFYALL", "DELETE", "DELETEALL"};

    public Table(int id, String name, NavType navType){
        super(id, name, navType);
        this.triggerIndexes = new TreeMap<>();
        this.fieldIndexes = new TreeMap<>();
        this.fields = new TreeMap<>();
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

}
