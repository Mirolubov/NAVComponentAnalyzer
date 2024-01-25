package com.company.navcomponentanalyzer.core.model.parser;

import com.company.navcomponentanalyzer.core.model.object.Form;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.object.Table;
import com.company.navcomponentanalyzer.core.model.object.element.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
    Fill nav objects with procedures, fields, triggers etc...
*/
public class BodyParser {
    private final static String PROCEDURE = " PROCEDURE ";
    private final static String FIELDS = "  FIELDS";
    private final static String CONTROLS = "  CONTROLS";
    private final static String KEYS = "  KEYS";
    private final static String GROUP_START = "  {";
    private final static String GROUP_END = "  }";
    private final static String VAR = "VAR";
    private final static String CODE = "  CODE";
    private final static String TRIGGER_PATTERN = ".*On([a-zA-Z]+)=.*";

    public static boolean newLine(String line) {
        return (line.isBlank());
    }

    public static boolean codeStart(String line) {
        return (line.indexOf(CODE) == 0);
    }

    public static boolean varStart(String line) {
        return (line.contains(VAR));
    }

    public static boolean fieldsRange(String line) {
        return line.equals(FIELDS);
    }

    public static boolean controlsRange(String line) {
        return line.equals(CONTROLS);
    }

    public static boolean keysRange(String line) {
        return line.equals(KEYS);
    }

    public static boolean groupEnd(String line) {
        return line.equals(GROUP_END);
    }

    public static String searchProcedure(String line) {
        int startPos = line.indexOf(PROCEDURE);
        if (startPos >= 0) {
            int endPos = line.indexOf('@');
            if (endPos > startPos) {
                return line.substring(startPos + PROCEDURE.length(), endPos);
            }
        }
        return "";
    }

    private static String searchTrigger(String line) {
        Pattern pattern = Pattern.compile(TRIGGER_PATTERN);
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            return "On" + matcher.group(1);
        } else {
            return "";
        }
    }

    public static String searchField(String line) {
        int startPos = line.indexOf(GROUP_START);
        if (startPos >= 0) {
            String[] parts = line.split(";");
            if(parts.length >= 3 ){
                String field = parts[2].strip();
                if(field.contains(" ")){
                    field = String.format("\"%s\"",field);
                }
                return field;
            }
        }
        return "";
    }

    private static String searchControl(String line) {
        int startPos = line.indexOf(GROUP_START);
        if (startPos >= 0) {
            String[] parts = line.split(";");
            if(parts.length >= 3 ){
                String control = parts[0].strip();
                control = control.replace("{ ", "");
                if(control.contains(" ")){
                    control = String.format("\"%s\"",control);
                }
                return control;
            }
        }
        return "";
    }

    public static void parseNavObjectBody(NavObject navObject) {
        Stream<String> lines = navObject.getBody().lines();
        int lineNo = 0;
        Boolean codeStart = false;
        Block varBlock = new Block();
        Block keyBlock = new Block();
        Block fieldBlock = new Block();
        Block controlBlock = new Block();
        StringBuilder procName = new StringBuilder();
        StringBuilder triggerName = new StringBuilder();
        StringBuilder fieldName = new StringBuilder();
        StringBuilder controlName = new StringBuilder();
        StringBuilder keyBody = null;
        if (navObject.isTable()) {
            keyBody = new StringBuilder();
        }
        for (Object line : lines.toArray()) {
            lineNo++;
            if(!codeStart) {
                if (BodyParser.codeStart((String) line)) {
                    codeStart = true;
                    continue;
                }
                if (fieldBlock.isNotStarted() && controlBlock.isNotStarted()) {
                    if (extractTrigger((String) line, lineNo, navObject, triggerName)) {
                        continue;
                    }
                }
            }
            if(codeStart) {
                if (extractVariable((String) line, lineNo, navObject, varBlock)) {
                    continue;
                }
                if (extractProcedure((String) line, lineNo, navObject, procName)) {
                    continue;
                }
            }
            if (navObject.isForm()) {
                if(extractControl((String)line, lineNo, (Form) navObject, controlBlock, controlName)){
                    continue;
                }
            }
            if (navObject.isTable()) {
                if(extractField((String)line, lineNo, (Table) navObject, fieldBlock, fieldName)){
                    continue;
                }
                if(extractKey((String) line, lineNo, keyBody, navObject, keyBlock)) {
                    continue;
                }
            }
        }
    }

    private static boolean extractControl(String line, int lineNo, Form formObject, Block controlBlock, StringBuilder controlName) {
        if(controlBlock.isFinished()) {
            return false;
        }
        if(controlBlock.isNotStarted()) {
            if (BodyParser.controlsRange(line)) {
                controlBlock.setStarted();
                return true;
            }
            return false;
        }

        if (!controlName.toString().isEmpty()) {
            Control control = formObject.getControls().get(controlName.toString());
            if(!control.appendBody(line)){
                controlName.setLength(0);
            }
            return true;
        }

        if (BodyParser.groupEnd(line)){
            controlBlock.setFinished();
            return true;
        }

        controlName.append(searchControl(line));
        if (!controlName.toString().isBlank()) {
            Control control = new Control(controlName.toString(), lineNo);
            formObject.getControls().put(controlName.toString(), control);
            if(!control.appendBody(line)){
                controlName.setLength(0);
            }
            return true;
        }
        return false;
    }

    private static boolean extractField(String line, int lineNo, Table tableObject, Block fieldBlock, StringBuilder fieldName) {
        if(fieldBlock.isFinished()) {
            return false;
        }
        if(fieldBlock.isNotStarted()) {
            if (BodyParser.fieldsRange(line)) {
                fieldBlock.setStarted();
                return true;
            }
            return false;
        }

        if (!fieldName.toString().isEmpty()) {
            Field field = tableObject.getFields().get(fieldName.toString());
            if(!field.appendBody(line)){
                fieldName.setLength(0);
            }
            return true;
        }

        if (BodyParser.groupEnd(line)){
            fieldBlock.setFinished();
            return true;
        }

        fieldName.append(searchField(line));
        if (!fieldName.toString().isBlank()) {
            Field field = new Field(fieldName.toString(), lineNo);
            tableObject.getFields().put(fieldName.toString(), field);
            if(!field.appendBody(line)){
                fieldName.setLength(0);
            }
            return true;
        }
        return false;
    }

    private static boolean extractKey(String line, int lineNo, StringBuilder keyBody, NavObject navObject, Block keyBlock) {
        if(keyBlock.isFinished()) {
            return false;
        }
        if(keyBlock.isNotStarted()) {
            if (BodyParser.keysRange(line)) {
                keyBlock.setStarted();
                keyBody.setLength(0);
                return true;
            }
            return false;
        }
        if (line.equals(GROUP_START)) {
            return true;
        }
        if (BodyParser.groupEnd(line)){
            keyBlock.setFinished();
            return true;
        }
        keyBody.append(line);
        if (line.contains("}")) {
            Index index = new Index(keyBody);
            index.setLineNo(lineNo);
            index.setTableNo(navObject.getId());
            ((Table) navObject).addKey(index);
            keyBody.setLength(0);
        }
        return false;
    }

    private static boolean extractTrigger(String line, int lineNo, NavObject navObject, StringBuilder triggerName) {
        if (!triggerName.toString().isEmpty()) {
            Trigger trigger = (Trigger)navObject.getProcedures().get(triggerName.toString());
            if(!trigger.appendBody(line)){
                triggerName.setLength(0);
            }
            return true;
        }
        triggerName.append(BodyParser.searchTrigger(line));
        if (!triggerName.toString().isBlank()) {
            navObject.getProcedureIndexes().put(triggerName.toString(), lineNo);
            Trigger trigger = new Trigger(triggerName.toString());
            trigger.setLineNo(lineNo);
            navObject.getProcedures().put(triggerName.toString(), trigger);
            trigger.appendBody(line);
            return true;
        }
        return false;
    }

    private static boolean extractProcedure(String line, int lineNo, NavObject navObject, StringBuilder procName) {
        if (!procName.toString().isEmpty()) {
            Procedure proc = navObject.getProcedures().get(procName.toString());
            if(!proc.appendBody(line)){
                procName.setLength(0);
            }
            return true;
        }
        procName.append(BodyParser.searchProcedure(line));
        if (!procName.toString().isBlank()) {
            navObject.getProcedureIndexes().put(procName.toString(), lineNo);
            Procedure proc = new Procedure(procName.toString());
            proc.setLineNo(lineNo);
            navObject.getProcedures().put(procName.toString(), proc);
            proc.appendBody(line);
            return true;
        }
        return false;
    }

    private static boolean extractVariable(String line, int lineNo, NavObject navObject, Block varBlock) {
        if(varBlock.isFinished()) {
            return false;
        }
        if (varBlock.isNotStarted()) {
            if (BodyParser.varStart(line)) {
                varBlock.setStarted();
                return true;
            }
        }
        if (BodyParser.newLine(line)) {
            varBlock.setFinished();
            return true;
        }
        if(line.contains("@")) {
            Var variable = new Var(line);
            variable.setLineNo(lineNo);
            navObject.getVarList().put(variable.getName(), variable);
            navObject.getVarIndexes().put(variable.getName(), lineNo);
            return true;
        }
        return false;
    }

    public static String clearComment(String line) {
        int comment  = line.indexOf("//");
        if (comment >= 0) {
            line = line.substring(0, comment);
        }
        return line;
    }
}
