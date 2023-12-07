package com.company.model;

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
        if (type.equals(NavType.Table)){
            return new Table(id, name, type);
        } else {
            return new NavObject(id, name, type);
        }
    }
}


