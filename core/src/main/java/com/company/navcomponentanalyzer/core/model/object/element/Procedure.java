package com.company.navcomponentanalyzer.core.model.object.element;

import com.company.navcomponentanalyzer.core.model.parser.BodyParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Procedure {
    private String name;
    private String body;
    private StringBuilder bodyBuilder;
    private int bodyBlock;
    private int lineNo;
    private int inLineNo;
    private boolean varBlock;
    private Map<String, Var> varList;
    private final static String BEGIN = "BEGIN";
    private final static String END = " END";

    public Procedure(String name) {
        this.name = name;
        bodyBuilder = new StringBuilder();
        bodyBlock = 0;
        lineNo = 0;
        inLineNo = 0;
        varBlock = false;
        varList = new HashMap<>();
    }

    public boolean appendBody(String line) {
        bodyBuilder.append(line);
        bodyBuilder.append("\r\n");
        inLineNo ++;
        line = BodyParser.clearComment(line);
        if (!BodyParser.searchProcedure(line).isEmpty()) {
            List<Var> vars = parseProcParameters(line);
            for(Var variable: vars){
                varList.put(variable.getName(), variable);
            }
            return true;
        }
        if (!varBlock) {
            varBlock = BodyParser.varStart(line);
            if(varBlock){
                return true;
            }
        }
        bodyBlock += blockBeginEnd(line);
        if (varBlock) {
            if (bodyBlock != 0) {
                varBlock = false;
            }else{
                if(line.contains("@")) {
                    Var variable = new Var(line);
                    variable.setLineNo(lineNo + inLineNo - 1);
                    varList.put(variable.getName(), variable);
                }
                return true;
            }
        }

        if (bodyBlock == 0) {
            finishBody();
            return false;
        }
        return true;
    }

    public void finishBody() {
        setBody(bodyBuilder.toString());
        bodyBuilder.setLength(0);
    }

    public static List<Var> parseProcParameters(String line) {
        List<Var> parameters = new ArrayList<>();

        String strParam = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        if (strParam.length() > 1) {
            String varLine;
            while (!strParam.isEmpty()) {
                int endParam = strParam.indexOf(';');
                if (endParam > 0) {
                    varLine = strParam.substring(0, endParam);
                    strParam = strParam.substring(endParam + 1);
                } else {
                    varLine = strParam;
                    strParam = "";
                }
                Parameter parameter = new Parameter(varLine);
                parameters.add(parameter);
            }
        }
        return parameters;
    }

    public static int blockBeginEnd(String line) {
        int retVal = (line.contains(BEGIN))?1:0;
        retVal -=  (line.contains(END))?1:0;
        return retVal;
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, Var> getVarList() {
        return varList;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }
}
