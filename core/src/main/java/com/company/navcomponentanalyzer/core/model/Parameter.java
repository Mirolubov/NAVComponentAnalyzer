package com.company.navcomponentanalyzer.core.model;

public class Parameter extends Var{
    private Boolean byVar;
    private final static String VAR = "VAR ";

    public Parameter(String line) {
        super();
        byVar = false;
        parseLine(line);
    }

    @Override
    protected void parseLine(String line) {
        if (line.contains(VAR)) {
            byVar = true;
            line = line.substring(VAR.length());
        }
        super.parseLine(line);
    }

    public Boolean getByVar() {
        return byVar;
    }
}
