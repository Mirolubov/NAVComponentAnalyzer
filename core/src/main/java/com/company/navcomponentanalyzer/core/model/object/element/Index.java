package com.company.navcomponentanalyzer.core.model.object.element;

import java.util.ArrayList;
import java.util.List;

public class Index {
    private final String CLUSTERED = "Clustered=Yes";
    private final String SUM_INDEX_FIELDS = "SumIndexFields=";
    private List<String> fields = new ArrayList<>();
    private boolean clustered;
    private boolean sift;
    private int lineNo;
    private int tableNo;
    private String tableName;

    private List<String> siftFields = new ArrayList<>();

    public Index(StringBuilder keyBody) {
        String body = keyBody.toString();
        body = body.replace('{',' ');
        body = body.replace('}',' ');
        body = body.strip();
        String[] indexParts = body.split(";");
        if(indexParts.length < 2) {
            return;
        }
        for (String field : indexParts[1].split(",")) {
            addField(field.strip());
        }
        if(indexParts.length < 3) {
            return;
        }
        setClustered(indexParts[2].contains(CLUSTERED));
        setSift(indexParts[2].contains(SUM_INDEX_FIELDS));
        if(sift) {
            String siftFields = indexParts[2].substring(SUM_INDEX_FIELDS.length());
            for (String field : siftFields.split(",")) {
                addSiftFields(field.strip());
            }

        }
    }

    public void addField(String field) {
        this.fields.add(field);
    }

    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

    public void setSift(boolean sift) {
        this.sift = sift;
    }

    public void addSiftFields(String siftField) {
        this.siftFields.add(siftField);
    }

    public boolean isClustered() {
        return clustered;
    }

    public boolean isSift() {
        return sift;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getSiftFields() {
        return siftFields;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
