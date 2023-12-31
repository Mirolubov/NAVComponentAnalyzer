package com.company.model;

import com.company.view.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class BodyParser {
    private final static String PROCEDURE = "PROCEDURE ";
    private final static String FIELDS = "  FIELDS";
    private final static String GROUP_START = "  {";
    private final static String GROUP_END = "  }";
    private final static String BEGIN = "BEGIN";
    private final static String END = " END";
    private final static String VAR = "VAR";
    private final static String CODE = "  CODE";
    private final static String procFinishChars = "() ;\r\n<>!=,.:";
    private final static String procStartChars = "() ;<>!=,";
    private final static String TRIGGER_VALIDATE = "OnValidate=";
    private final static String ONVALIDATE = "OnValidate";
    public final static String CAPTIONML_START = "CaptionML=";


    public static boolean newLine(String line) {
        return (line.isBlank());
    }

    public static boolean codeStart(String line) {
        return (line.indexOf(CODE) == 0);
    }

    public static boolean varStart(String line) {
        return (line.contains(VAR));
    }

    public static boolean triggerStart(String line) {
        return (line.contains(TRIGGER_VALIDATE));
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

    public static int blockBeginEnd(String line) {
        int retVal = (line.contains(BEGIN))?1:0;
        retVal -=  (line.contains(END))?1:0;
        return retVal;
    }

    public static boolean fieldsRange(String line) {
        return line.equals(FIELDS);
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
        return null;
    }

    public static String searchTriggerName() {
        return ONVALIDATE;
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
        return null;
    }

    public static List<Var> parseProcParameters(String line) {
        List<Var> parameters = new ArrayList<>();

        String strParam = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
        if (strParam.length() > 1) {
            String varLine;
            while (!strParam.isEmpty()) {
                int endParam = strParam.indexOf(';');
                if (endParam > 0) {
                    varLine = strParam.substring(0, endParam);
                    strParam = strParam.substring(endParam + 1);
                } else {
                    varLine = strParam;
                    strParam = "";
                }
                Parameter parameter = new Parameter(varLine);
                parameters.add(parameter);
            }
        }
        return parameters;
    }

    public static void parseProcedures(NavObject navObject) {
        Map<String, Var> varObjList = navObject.getVarList();
        Map<String, Procedure> procedureList = navObject.getProcedures();
        if (procedureList != null) {
            for (Map.Entry<String, Procedure> procEntry : procedureList.entrySet()) {
                Procedure procedure = procEntry.getValue();
                parseProcedure(procedure, varObjList);
            }
        }
        if(navObject.getNavType().equals(NavType.Table) && navObject instanceof Table) {
            Map<String, Field> fieldList = ((Table) navObject).getFields();
            for (Map.Entry<String, Field> fieldEntry : fieldList.entrySet()) {
                Field field = fieldEntry.getValue();
                List<Trigger> triggers = field.getTriggers();
                for(Trigger trigger: triggers){
                    parseProcedure(trigger, varObjList);
                }
            }
        }
//        progressBar.getProgressBar().setValue(i++);
    }

    private static void parseProcedure(Procedure procedure, Map<String, Var> varObjList) {
        if (procedure == null) {
            return;
        }
        if (procedure.getBody() == null) {
            return;
        }

        Map<String, Var> varList = procedure.getVarList();
        for(Map.Entry<String, Var> variable: varList.entrySet()) {
            parseProcedureFillVariable(variable, procedure);
        }
        for(Map.Entry<String, Var> variable: varObjList.entrySet()) {
            parseProcedureFillVariable(variable, procedure);
        }
    }

    private static void parseProcedureFillVariable(Map.Entry<String, Var> variable, Procedure procedure) {
        if(!variable.getValue().isNavObject()) {
            return;
        }
        int lineOffset = procedure.getLineNo();
        Stream<String> lines = procedure.getBody().lines();
        int lineNo = 0;
        for (Object object : lines.toArray()) {
            String line = (String)object;
            line = clearComment(line);
            String searchVarSpace = " " + variable.getKey() + ".";
            String searchVarBrace = "(" + variable.getKey() + ".";
            int posSpace = line.indexOf(searchVarSpace);
            int posBrace = line.indexOf(searchVarBrace);
            int pos = Math.min(posSpace < 0 ? 999 : posSpace, posBrace < 0 ? 999 : posBrace);
            if (pos < 999){
                String procName = line.substring(pos + searchVarSpace.length());
                if(procName.isBlank()) {
                    lineNo ++;
                    continue;
                }
                int porocedureFinish = 0;
                if (procName.charAt(0) == '"'){
                    porocedureFinish = procName.indexOf('"', 1) + 1;
                }else {
                    for (char ch : procName.toCharArray()) {
                        if (procFinishChars.contains(Character.toString(ch))) {
                            break;
                        }
                        porocedureFinish++;
                    }
                }
                try {
                    procName = procName.substring(0, porocedureFinish);
                }catch (StringIndexOutOfBoundsException e){
                    System.out.println("Exception in parseProcedureFillVariable:");
                    System.out.printf("line: %s\r\n", line);
                    System.out.printf("variable: %s\r\n", variable.getKey());
                    System.out.printf("procName: %s\r\n", procName);
                    lineNo ++;
                    continue;
                }

                if(variable.getValue().getExecutes().containsKey(procName)){
                    List<Integer> pl = variable.getValue().getExecutes().get(procName);
                    pl.add(lineOffset + lineNo);
                }else{
                    List<Integer> pl = new ArrayList<>();
                    pl.add(lineOffset + lineNo);
                    variable.getValue().getExecutes().put(procName, pl);
                }
            }
            lineNo ++;
        }
    }

    private static String clearComment(String line) {
        int comment  = line.indexOf("//");
        if (comment >= 0) {
            line = line.substring(0, comment);
        }
        return line;
    }

    public static List<Integer> getNameUsesInBody(String body, String searchName) {
        int lineNo = 0;
        List<Integer> ret = new ArrayList<>();
        Object[] lines = body.lines().toArray();
        for (Object object : lines) {
            String line = (String) object;
            line = clearComment(line);
            searchInLine(searchName, line, ret, lineNo++);
        }
        return ret;
    }

    private static void searchInLine(String searchName, String line, List<Integer> ret, int lineNo) {
        for (char ch: procStartChars.toCharArray()) {
            String searchStr = String.format("%c%s", ch, searchName);
            if (searchInLineText(searchName, line, ret, lineNo, searchStr)) {
                break;
            }
        }
    }

    private static boolean searchInLineText(String searchName, String line, List<Integer> ret, int lineNo, String text) {
        int posText = line.indexOf(text);
        if (posText >= 0) {
            String strEnd = line.substring(posText + text.length());
            if(strEnd.isBlank()) {
                ret.add(lineNo);
                return true;
            }

            if (procFinishChars.contains(strEnd.substring(0, 1))) {
                ret.add(lineNo);
                return true;
            } else {
                searchInLine(searchName, strEnd, ret, lineNo);
            }
        }
        return false;
    }

    public static void parseNavObjectBody(NavObject navObject) {
        String body = navObject.getBody();
        NavType navType = navObject.getNavType();
        Map<String, Var> varList = navObject.getVarList();
        Map<String, Integer> varIndexes = navObject.getVarIndexes();
        Map<String, Integer> procedureIndexes = navObject.getProcedureIndexes();
        Map<String, Procedure> procedures = navObject.getProcedures();
        Map<String, Integer> fieldIndexes = null;
        Map<String, Field> fields = null;
        if (navObject instanceof Table) {
            fieldIndexes = ((Table)navObject).getFieldIndexes();
            fields = ((Table)navObject).getFields();
        }

        Stream<String> lines = body.lines();
        int lineNo = 0;
        boolean fieldRange = false;
        boolean codeStart = false;
        boolean varBlock = false;
        boolean varLoaded = false;
        boolean captionBlock = false;
        String captionMLEndChar = "";
        Procedure proc = null;
        Field field = null;
        Trigger trigger = null;
        for (Object line : lines.toArray()) {
            lineNo++;
            if(!codeStart && !varLoaded) {
                codeStart = BodyParser.codeStart((String) line);
            }
            if(codeStart) {
                if (!varBlock) {
                    varBlock = BodyParser.varStart((String)line);
                    if (varBlock) {
                        continue;
                    }
                }
                if (BodyParser.newLine((String)line)) {
                    varLoaded = true;
                    codeStart = false;
                    continue;
                }
                if (varBlock) {
                    if(((String) line).contains("@")) {
                        Var variable = new Var((String) line);
                        variable.setLineNo(lineNo);
                        varList.put(variable.getName(), variable);
                        varIndexes.put(variable.getName(), lineNo);
                        continue;
                    }
                }
            }

            String procName = BodyParser.searchProcedure((String)line);
            if (procName != null) {
                procedureIndexes.put(procName, lineNo);
                proc = new Procedure(procName);
                proc.setLineNo(lineNo);
                procedures.put(procName, proc);
            }
            if (proc != null) {
                if(!proc.appendBody((String)line)){
                    proc = null;
                }
                continue;
            }
            if (navType == NavType.Table) {
                if (fieldRange) {
                    String fieldName = BodyParser.searchField((String) line);
                    if (fieldName != null && fieldIndexes != null) {
                        fieldIndexes.put(fieldName, lineNo);
                        field = new Field(fieldName, lineNo);
                        fields.put(fieldName, field);
                    }
                    if(BodyParser.triggerStart((String) line)){
                        trigger = new Trigger(BodyParser.searchTriggerName());
                        trigger.setLineNo(lineNo);
                        if (field != null) {
                            field.getTriggers().add(trigger);
                        }
                    }
                    if (trigger != null) {
                        if(!trigger.appendBody((String)line)){
                            trigger = null;
                        }
                        continue;
                    }
                    if (!captionBlock) {
                        captionMLEndChar = BodyParser.captionMLStart((String) line);
                        captionBlock = !(captionMLEndChar.isBlank());
                    }
                    if(captionBlock){
                        field.addCaptionLine((String) line);
                        captionBlock = !BodyParser.captionMLEnd((String) line, captionMLEndChar);
                        if(!captionBlock) {
                            captionMLEndChar = "";
                            field.parseCaption();
                        }
                    }
                    if (BodyParser.groupEnd((String) line)){
                        if(captionBlock) {
                            captionBlock = false;
                            captionMLEndChar = "";
                            field.parseCaption();
                        }
                        fieldRange = false;
                    }
                } else if (BodyParser.fieldsRange((String) line)) {
                    fieldRange = true;
                }

            }
        }
    }

    public static int containTransaction(String body){
        if(body == null)
            return 0;
        if(body.isBlank())
            return 0;
        for(int i =0; i < Table.SYSTEM_PROC.length; i++){
            Object [] lines = body.lines().toArray();
            for(int lineNo = 0; lineNo < lines.length; lineNo++) {
                String line = (String)lines[lineNo];
                if (line.contains(String.format(" %s", Table.SYSTEM_PROC[i])) ||
                        line.contains(String.format("(%s", Table.SYSTEM_PROC[i]))) {
                    return lineNo;
                }
            }
        }
        return 0;
    }

}
