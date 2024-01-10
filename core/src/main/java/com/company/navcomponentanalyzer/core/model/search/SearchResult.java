package com.company.navcomponentanalyzer.core.model.search;

import java.util.Objects;

public class SearchResult {
    private String type;
    private String name;
    private String no;
    private String line;
    private String text;

    public SearchResult() {
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getNo() {
        return no;
    }

    public String getLine() {
        return line;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SearchResult) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.no, that.no) &&
                Objects.equals(this.line, that.line) &&
                Objects.equals(this.text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, no, line, text);
    }

    @Override
    public String toString() {
        return "SearchResult[" +
                "type=" + type + ", " +
                "name=" + name + ", " +
                "no=" + no + ", " +
                "line=" + line + ", " +
                "text=" + text + ']';
    }

}
