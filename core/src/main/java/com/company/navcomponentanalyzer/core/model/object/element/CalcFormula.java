package com.company.navcomponentanalyzer.core.model.object.element;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcFormula {
    private final List<String> columns;
    private String agregate;
    private String tableName;
    private String fieldName;
    private final String body;

    public CalcFormula(String calcFormulaString) {
        body = calcFormulaString;
        calcFormulaString = extractAgregate(calcFormulaString);
        calcFormulaString = extractTable(calcFormulaString);
        columns = new ArrayList<>();
        extractColumns(calcFormulaString);
    }

    public String getAgregate() {
        return agregate;
    }

    public String getTableName() {
        return tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public List<String> getColumns() {
        return columns;
    }

    private String extractAgregate(String formula) {
        int brace = formula.indexOf("(");
        if (brace == -1) {
            return "";
        }
        agregate = formula.substring(0, brace);
        formula = formula.substring(brace + 1, formula.length() - 1);
        return formula;
    }

    private String extractTable(String formula) {
        String table;
        int wherePos = formula.indexOf(" WHERE ");
        if (wherePos != -1) {
            table = formula.substring(0, wherePos);
            formula = formula.substring(wherePos + 8, formula.length() - 1);
        } else {
            table = formula;
        }
        Pattern pattern = Pattern.compile("\"([^\"]*?)\"\\.(.*)");
        Matcher matcher = pattern.matcher(table);
        if (matcher.find()) {
            tableName = matcher.group(1);
            fieldName = matcher.group(2).replace("\"", "");
        } else {
            int dotPos = table.indexOf(".");
            int quotPos = table.indexOf("\"");
            if (dotPos == -1) {
                tableName = table;
                fieldName = "";
            } else if (dotPos < quotPos || quotPos == -1) {
                tableName = table.substring(0, dotPos);
                fieldName = table.substring(dotPos + 1).replace("\"", "");
            }
        }
        return formula;
    }

    private void extractColumns(String calcFormulaString) {
        Pattern pattern = Pattern.compile("([^=]+)=.*[,\\r\\n]*");
        Matcher matcher = pattern.matcher(calcFormulaString);
        while (matcher.find()) {
            columns.add(matcher.group(1).trim());
        }

    }

    public String getBody() {
        return body;
    }
}
