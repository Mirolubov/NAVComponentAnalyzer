package com.company.navcomponentanalyzer.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Procedure {
    private final String name;
    private String body;
    private final StringBuilder bodyBuilder;
    private int bodyBlock;
    private int lineNo;
    private int inLineNo;
    private boolean varBlock;
    private final Map<String, Var> varList;

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
        int comment = line.indexOf("//");
        if (comment >= 0) {
            line = line.substring(0, comment);
        }
        if (BodyParser.searchProcedure(line) != null) {
            List<Var> vars = BodyParser.parseProcParameters(line);
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
        bodyBlock += BodyParser.blockBeginEnd(line);
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
