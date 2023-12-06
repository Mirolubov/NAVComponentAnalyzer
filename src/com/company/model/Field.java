package com.company.model;

import com.company.config.AppProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Field {
    private final String name;
    private String caption;
    private final int lineNo;
    private final ArrayList<Trigger> triggers;
    private final Map<String, String> captions;

    public Field(String name, int lineNo) {
        this.name = name;
        this.lineNo = lineNo;
        triggers = new ArrayList<>();
        captions = new HashMap<>();
        caption = "";
    }

    public Map<String, String> getCaptions() {
        return captions;
    }

    public ArrayList<Trigger> getTriggers() {
        return triggers;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void addCaptionLine(String line) {
        caption += line;
    }

    public String getCaption() {
        return caption;
    }

    public void parseCaption() {
        if (caption.isBlank())
            return;
        AppProperties prop = AppProperties.initAppProperties();
        String regex = String.format("(%s)=([^;\\]\\}]+)", prop.getCaptionML());
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(caption);

        while (matcher.find()) {
            String keyValue = matcher.group(1);
            String valValue = matcher.group(2);

            if (keyValue != null && valValue != null) {
                captions.put(keyValue, valValue);
            }
        }
    }
}
