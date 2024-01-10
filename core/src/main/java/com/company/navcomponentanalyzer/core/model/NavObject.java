package com.company.navcomponentanalyzer.core.model;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class NavObject implements Comparable<NavObject> {
    protected final int id;
    protected final String name;
    protected final NavType navType;
    protected final Map<String, Integer> procedureIndexes;
    protected final Map<String, Procedure> procedures;
    protected final Map<String, Var> varList;
    protected final Map<String, Integer> varIndexes;
    private String body;
    private long lineCount;

    public NavObject(int id, String name, NavType navType) {
        this.id = id;
        this.name = name;
        this.navType = navType;
        this.procedureIndexes = new TreeMap<>();
        this.procedures = new TreeMap<>();
        this.varList = new TreeMap<>();
        this.varIndexes = new TreeMap<>();
    }

    public Map<String, Integer> getVarIndexes() {
        return varIndexes;
    }

    public Map<String, Procedure> getProcedures() {
        return procedures;
    }

    public Map<String, Integer> getProcedureIndexes() {
        return procedureIndexes;
    }

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public long getLineCount() {
        return lineCount;
    }

    public void setLineCount(long lineCount) {
        this.lineCount = lineCount;
    }

    public void setBody(String body) {
        this.body = body;
        this.setLineCount(body.lines().count());
        BodyParser.parseNavObjectBody(this);
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", name, id);
    }

    public String getName() {
        return name;
    }

    public NavType getNavType() {
        return navType;
    }

    public Map<String, Var> getVarList() {
        return varList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof NavObject))
            return false;
        NavObject navObject = (NavObject) o;
        return id == navObject.id && navType == navObject.navType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, navType);
    }

    @Override
    public int compareTo(NavObject navObject) {
        if(navObject.getNavType().equals(this.getNavType())){
            return this.getId() - navObject.getId();
        } else {
            return this.getNavType().toString().compareTo(navObject.getNavType().toString());
        }
    }
}
