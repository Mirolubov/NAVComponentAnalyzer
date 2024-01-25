package com.company.navcomponentanalyzer.core.model.parser;

import com.company.navcomponentanalyzer.core.model.object.Form;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.object.Table;
import com.company.navcomponentanalyzer.core.model.object.element.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/*
    Fill all procedure variable with executes inside
*/
public class ProcedureParser {
    private final static String procFinishChars = "() ;\r\n<>!=,.:";
    private final static String procStartChars = "() ;<>!=,";

    public static void parseProcedures(NavObject navObject) {
        Map<String, Var> varObjList = navObject.getVarList();
        Map<String, Procedure> procedureList = navObject.getProcedures();
        if (procedureList != null) {
            for (Map.Entry<String, Procedure> procEntry : procedureList.entrySet()) {
                Procedure procedure = procEntry.getValue();
                parseProcedure(procedure, varObjList);
            }
        }
        if(navObject.isTable() && navObject instanceof Table) {
            Map<String, Field> fieldList = ((Table) navObject).getFields();
            for (Map.Entry<String, Field> fieldEntry : fieldList.entrySet()) {
                Field field = fieldEntry.getValue();
                List<Trigger> triggers = field.getTriggers();
                for(Trigger trigger: triggers){
                    parseProcedure(trigger, varObjList);
                }
            }
        }
        if(navObject.isForm() && navObject instanceof Form) {
            Map<String, Control> controlList = ((Form) navObject).getControls();
            for (Map.Entry<String, Control> controlEntry : controlList.entrySet()) {
                Control control = controlEntry.getValue();
                List<Trigger> triggers = control.getTriggers();
                for(Trigger trigger: triggers){
                    parseProcedure(trigger, varObjList);
                }
            }
        }
    }

    private static void parseProcedure(Procedure procedure, Map<String, Var> varObjList) {
        if (procedure == null) {
            return;
        }
        if (procedure.getBody() == null) {
            return;
        }
        //Local variables
        Map<String, Var> varList = procedure.getVarList();
        for(Map.Entry<String, Var> variable: varList.entrySet()) {
            parseProcedureFillVariable(variable, procedure);
        }
        //Global variables
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
            line = BodyParser.clearComment(line);
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
}
