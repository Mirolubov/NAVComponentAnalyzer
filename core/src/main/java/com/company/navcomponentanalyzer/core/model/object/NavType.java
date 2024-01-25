package com.company.navcomponentanalyzer.core.model.object;

public enum NavType {
    Table,
    Codeunit,
    Report,
    Page,
    Form,
    XMLport,
    MenuSuite,
    Query,
    Dataport;

    public static NavType fromString(String strNavType) {
        switch (strNavType){
            case "Table":
            case "Record":
                return NavType.Table;
            case "Codeunit": return NavType.Codeunit;
            case "Report": return NavType.Report;
            case "Page": return NavType.Page;
            case "Form": return NavType.Form;
            case "XMLport": return NavType.XMLport;
            case "MenuSuite": return NavType.MenuSuite;
            case "Query": return NavType.Query;
            case "Dataport": return NavType.Dataport;
            default: return null;
        }
    }

    public static boolean checkNavObject(String type) {
        return (NavType.fromString(type) != null);
    }

}
