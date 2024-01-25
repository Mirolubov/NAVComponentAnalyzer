package com.company.navcomponentanalyzer.core.model.object.element;

import java.util.ArrayList;
import java.util.List;

public class CalcFormula {
    private final String agregate;
    private final String tableName;
    private final String fieldName;
    private final List<String> columns;

    public CalcFormula(String calcFormulaString) {
        agregate = "";
        tableName = "";
        fieldName = "";
        columns = new ArrayList<>();
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
}
