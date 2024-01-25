package com.company.navcomponentanalyzer.core.model;

import com.company.navcomponentanalyzer.core.model.object.Form;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.object.NavType;
import com.company.navcomponentanalyzer.core.model.object.Table;

import java.util.Set;
import java.util.TreeSet;

public class NavObjects {
    private final Set<NavObject> navObjectsList;
    private NavObject selectedObject;

    public NavObjects() {
        navObjectsList = new TreeSet<>();
    }

    
    public NavObject add(int id, String name, String strNavType, String body) {
        NavType navType = NavType.fromString(strNavType);
        if(navType == null)
            return null;
        NavObject newNavObject = createNavObject(id, name, navType);
        newNavObject.setBody(body);
        navObjectsList.add(newNavObject);
        return newNavObject;
    }

    public void removaAll() {
        navObjectsList.clear();
    }


    public Set<NavObject> getNavObjectsList() {
        return navObjectsList;
    }

    public NavObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(NavObject selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getCount() {
        return navObjectsList.size();
    }

    public static NavObject createNavObject(Integer id, String name, NavType type) {
        if(type == null)
            return null;
        switch (type){
            case Form:
                return new Form(id, name, type);
            case Table:
                return new Table(id, name, type);
            default:
                return new NavObject(id, name, type);
        }
    }
}


