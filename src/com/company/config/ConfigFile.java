//Copyright 2022 Artiom Mirolyubov
//Licensed under the Apache License, Version 2.0
package com.company.config;

import java.io.*;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map;

public class ConfigFile {
    private static final ConfigFile INSTANCE = new ConfigFile();
    private final String fileName = "NavComponentAnalyzer.config";
    private final TreeMap<String, String> configValues;
    private static final String THEME = "Theme";
    private static final String RECENT_FILE = "Recent File";

    public void setTheme(String value) {
        setConfigValue(THEME, value);
    }

    public String getTheme() {
        return getConfigValue(THEME);
    }

    public void setRecentFile(String value) {
        setConfigValue(RECENT_FILE + "1", value);
    }

    public ArrayList<String> getRecentFiles() {
        ArrayList<String> recentFiles = new ArrayList<>();
        String recentKey;
        for (Map.Entry<String, String> entry : configValues.entrySet()) {
            recentKey = entry.getKey();
            if(recentKey.startsWith(RECENT_FILE))
                recentFiles.add(entry.getValue());
        }
        return recentFiles;
    }
    public String getConfigValue(String key) {
        return configValues.get(key);
    }

    public void setConfigValue(String key, String value) {
        if(!configValues.containsKey(key)){
            configValues.put(key, value);
            return;
        }
        if(configValues.get(key).equals(value)) {
            return;
        }
        int line = 1;
        String recentValue;
        String prevValue = value;
        while((recentValue = configValues.get(key)) != null && line < 10){
            configValues.put(key, prevValue);
            key = RECENT_FILE + (++line);
            prevValue = recentValue;
        }
    }

    public static ConfigFile getInstance() {
        return INSTANCE;
    }

    public void closeAndSaveConfig(){
        OutputStream outputStream = null;
        try {
            File file = new File(fileName);
            outputStream = new FileOutputStream(file);
            writeToOutputStream(outputStream);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try{
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ConfigFile() {
        configValues = new TreeMap<>();
        InputStream inputStream = null;
        try {
            File file = new File(fileName);
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    readFromInputStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void readFromInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split("=", 2);
                if(!splitLine[0].isEmpty() && !splitLine[1].isEmpty())
                    setConfigValue(splitLine[0], splitLine[1]);
            }
        }
    }

    private void writeToOutputStream(OutputStream outputStream) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            if(configValues.containsKey(THEME)){
                bw.write(configValues.ceilingEntry(THEME).toString());
                bw.newLine();
            }
            int n = 0;
            NavigableMap<String, String> dmap = configValues.descendingMap();
            for (Map.Entry<String, String> entry : dmap.entrySet()) {
                if (!entry.getKey().equals(THEME)) {
                    bw.write(entry.toString());
                    bw.newLine();
                    if (n++ > 5) break;
                }
            }
        }
    }
}
