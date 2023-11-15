package com.company.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Var {
    private String name;
    private String type;
    private String exceptionType;
    private Integer objectId;
    private Boolean temporary;
    private Boolean navObject;
    private final static String TEMPORARY = "TEMPORARY";
    private final Map<String, List<Integer>> executes;

    public Var() {
        name = "";
        type = "";
        exceptionType = "";
        objectId = 0;
        temporary = false;
        navObject = false;
        executes = new HashMap<>();
    }

    public Var(String line) {
        name = "";
        type = "";
        exceptionType = "";
        objectId = 0;
        temporary = false;
        navObject = false;
        executes = new HashMap<>();
        parseLine(line);
    }


    protected void parseLine(String line){
        int comment = line.indexOf("//");
        if (comment >= 0) {
            line = line.substring(0, comment);
        }
        try {
            name = line.substring(0, line.indexOf('@')).strip();
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Exception in parseLine:");
            System.out.printf("line: %s\r\n", line);
        }

        String varType = line.substring(line.indexOf(':') + 1)
                .replace(";", "")
                .strip();
        if (varType.contains(TEMPORARY)) {
            temporary = true;
            varType = varType.substring(TEMPORARY.length()).strip();
        }
        String[] varSepType = varType.split(" ");
        setType(varSepType[0]);
        navObject = NavType.checkNavObject(getType());
        int varObjectId = 0;
        if (varSepType.length > 1){
            try {
                varObjectId = Integer.parseInt(varSepType[1]);
            } catch (NumberFormatException e) {
                exceptionType = varType.substring(type.length()).strip();
            }
        }

        objectId = varObjectId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if(type.equals("Record")) {
            this.type = "Table";
        }else {
            this.type = type;
        }
    }

    public Integer getObjectId() {
        return objectId;
    }

    public Boolean getTemporary() {
        return temporary;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public Map<String, List<Integer>> getExecutes() {
        return executes;
    }

    public Boolean isNavObject() {
        return navObject;
    }
}
