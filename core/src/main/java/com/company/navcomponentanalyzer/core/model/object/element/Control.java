package com.company.navcomponentanalyzer.core.model.object.element;

import com.company.navcomponentanalyzer.core.config.AppProperties;
import com.company.navcomponentanalyzer.core.model.parser.BodyParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Control {
    private final static String CAPTIONML_START = "CaptionML=";
    private final static String TRIGGER_PATTERN = ".*On([a-zA-Z]+)=.*";

    private String name;
    private String caption;
    private int lineNo;
    private ArrayList<Trigger> triggers;
    private Map<String, String> captions;
    private String body;
    private StringBuilder bodyBuilder;
    private Trigger trigger;
    private int inLineNo;
    private boolean captionBlock;
    private String captionMLEndChar;
    private int bodyBlock;

    public Control(String name, int lineNo) {
        this.name = name;
        this.lineNo = lineNo;
        this.inLineNo = 0;
        triggers = new ArrayList<>();
        captions = new HashMap<>();
        bodyBuilder = new StringBuilder();
        caption = "";
        captionBlock = false;
        bodyBlock = 0;
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

    public boolean appendBody(String line) {
        bodyBuilder.append(line);
        bodyBuilder.append("\r\n");
        line = BodyParser.clearComment(line);
        inLineNo++;
        bodyBlock += blockBeginEnd(line);

        String triggerName = triggerStart(line);
        if(triggerName != null){
            trigger = new Trigger(triggerName);
            trigger.setLineNo(lineNo + inLineNo - 1);
            getTriggers().add(trigger);
        }
        if (trigger != null) {
            if(!trigger.appendBody(line)){
                trigger = null;
            }
            return true;
        }
        if (!captionBlock) {
            captionMLEndChar = captionMLStart(line);
            captionBlock = !(captionMLEndChar.isBlank());
        }
        if(captionBlock){
            addCaptionLine(line);
            captionBlock = !captionMLEnd(line, captionMLEndChar);
            if(!captionBlock) {
                captionMLEndChar = "";
                parseCaption();
            }
        }

        if (bodyBlock == 0) {
            finishBody();
            return false;
        }
        return true;
    }

    private static long blockBeginEnd(String line) {
        long count = line.chars().filter(ch -> ch == '{').count();
        count -= line.chars().filter(ch -> ch == '}').count();
        return count;
    }

    public void finishBody() {
        setBody(bodyBuilder.toString());
        bodyBuilder.setLength(0);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static String triggerStart(String line) {
        Pattern pattern = Pattern.compile(TRIGGER_PATTERN);
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            return "On" + matcher.group(1);
        } else {
            return null;
        }
    }

    public static String captionMLStart(String line) {
        int pos = line.indexOf(CAPTIONML_START);
        if (pos == -1) {
            return "";
        }
        if (line.charAt(pos + CAPTIONML_START.length()) == '[') {
            return "]";
        } else {
            return ";";
        }
    }

    public static boolean captionMLEnd(String line, String mlEndChar) {
        return (line.contains(mlEndChar.toString()));
    }


}
