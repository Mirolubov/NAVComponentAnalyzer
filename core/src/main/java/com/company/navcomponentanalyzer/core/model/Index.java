package com.company.navcomponentanalyzer.core.model;

import java.util.ArrayList;
import java.util.List;

public class Index {
    private final String CLUSTERED = "Clustered=Yes";
    private final String SUM_INDEX_FIELDS = "SumIndexFields=";
    private List<String> fields = new ArrayList<>();
    private boolean clustered;
    private boolean sift;
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
            addField(field);
        }
        if(indexParts.length < 3) {
            return;
        }
        setClustered(indexParts[2].contains(CLUSTERED));
        setSift(indexParts[2].contains(SUM_INDEX_FIELDS));
        if(sift) {
            String siftFields = indexParts[2].substring(SUM_INDEX_FIELDS.length());
            for (String field : siftFields.split(",")) {
                addSiftFields(field);
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
}
