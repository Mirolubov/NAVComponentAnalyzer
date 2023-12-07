package com.company.model;

import com.company.config.AppProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Var {
    private String name;
    private String type;
    private String exceptionType;
    private Integer objectId;
    private Boolean temporary;
    private Boolean navObject;
    private int lineNo;
    private final static String TEMPORARY = "TEMPORARY";
    public final static String CONST_TYPE = "TextConst";
    private final Map<String, List<Integer>> executes;
    private final Map<String, String> captions;

    public Var() {
        name = "";
        type = "";
        exceptionType = "";
        objectId = 0;
        lineNo = 0;
        temporary = false;
        navObject = false;
        executes = new HashMap<>();
        captions = new HashMap<>();
    }

    public Var(String line) {
        name = "";
        type = "";
        exceptionType = "";
        objectId = 0;
        lineNo = 0;
        temporary = false;
        navObject = false;
        executes = new HashMap<>();
        captions = new HashMap<>();
        parseLine(line);
    }


    protected void parseLine(String line){
        int comment = line.indexOf("//");
        if (comment >= 0 && line.indexOf("://") == -1) {
            line = line.substring(0, comment);
        }
        try {
            name = line.substring(0, line.indexOf('@')).strip();
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Exception in parseLine:");
            System.out.printf("line: %s\r\n", line);
            return;
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
        if (varSepType[0].equals(CONST_TYPE)) {

            AppProperties prop = AppProperties.initAppProperties();
            String regex = String.format("(%s)=([^;]+)", prop.getCaptionML());
            Pattern pattern = Pattern.compile(regex);
            String constString;
            try {
                constString = line.substring(line.indexOf(CONST_TYPE) + CONST_TYPE.length() + 2, line.length() - 2).strip();
            } catch(StringIndexOutOfBoundsException e) {
                //System.out.println("Try get const values exception - StringIndexOutOfBoundsException: " + e.getMessage());
                //System.out.printf("line: %s\r\n", line);
                return;
            }
            Matcher matcher = pattern.matcher(constString);

            while (matcher.find()) {
                String keyValue = matcher.group(1);
                String valValue = matcher.group(2);

                if (keyValue != null && valValue != null) {
                    captions.put(keyValue, valValue);
                }
            }

        }
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

    public Map<String, String> getCaptions() {
        return captions;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }
}
