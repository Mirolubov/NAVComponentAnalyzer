package com.company.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        int end = -1;
        int start = caption.indexOf(BodyParser.CAPTIONML_START + "[");
        if (start == -1) {
            start = caption.indexOf(BodyParser.CAPTIONML_START);
            if (start != -1) {
                start += BodyParser.CAPTIONML_START.length();
                end = caption.indexOf(';', start);
            }
        }else {
            start += BodyParser.CAPTIONML_START.length() + 1;
            end = caption.indexOf(']', start);
        }
        if (start == -1) {
            return;
        }
        if (end == -1) {
            end = caption.length();
        }

        caption = caption.substring(start, end);

        String[] parts = caption.split(";");
        for (String part : parts) {
            String[] values = part.strip().split("=");
            if(values.length == 2) {
                captions.put(values[0].strip(), values[1].strip());
            }
        }
    }
}
