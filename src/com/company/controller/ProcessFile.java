package com.company.controller;

import com.company.model.NavObjects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

public class ProcessFile {
    private final NavObjects navObjects;
    private final StringBuilder bodyBuilder;
    private int id;
    private String name;
    private String strNavType;

    public ProcessFile() {
        bodyBuilder = new StringBuilder();
        navObjects = new NavObjects();
        clear();
    }

    public void process(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file, Charset.forName("CP866")));
            String line;
            while ((line = reader.readLine()) != null) {
                fillNavObjectList(line);
            }
            reader.close();
        }catch  (IOException e){
            e.printStackTrace();
        }
    }

    private void fillNavObjectList(String line) {
        String[] parts = line.split(" ");
        if(parts.length >= 4 )
            if(parts[0].equals("OBJECT")){
                clear();
                strNavType = parts[1];
                id = Integer.parseInt(parts[2]);
                StringBuilder nameBuilder = new StringBuilder();
                for(int i=3; i < parts.length; i ++) {
                    nameBuilder.append(parts[i]);
                    nameBuilder.append(" ");
                }
                name = nameBuilder.toString();
            }
        bodyBuilder.append(line);
        bodyBuilder.append("\r\n");
        if(line.equals("}")){
            navObjects.add(id, name, strNavType, bodyBuilder.toString());
            clear();
        }
    }

    public NavObjects getNavObjects() {
        return navObjects;
    }

    private void clear() {
        id = 0;
        name = "";
        strNavType = "";
        bodyBuilder.setLength(0);
    }
}

