//Copyright 2022 Artiom Mirolyubov
//Licensed under the Apache License, Version 2.0

package com.company;

import com.company.config.ArgumentParser;
import com.company.model.NavObjects;
import com.company.model.process.FileLoader;
import com.company.view.MainFrame;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main{

    public static void main(String[] args) {
        boolean consoleStyle = false;
        List<String> files = null;
        String folder = "";
        ArgumentParser argumentParser = new ArgumentParser(args);
        if (argumentParser.hasArguments()) {
            if (argumentParser.showHelp()) {
                displayHelpMessage();
            }
            folder = argumentParser.extractNextFolder();
            files = argumentParser.extractNextFiles();
        }
        MainFrame mainFrame = null;
        NavObjects navObjects = new NavObjects();
        if(!consoleStyle) {
            mainFrame = new MainFrame(navObjects);
        }
        if (files != null) {
            for (String file : files) {
                FileLoader fileLoader = new FileLoader(String.format("%s/%s", folder, file), mainFrame, navObjects);
                fileLoader.processFile();
            }
        }
    }

    private static void displayHelpMessage() {
        try (InputStream in = Main.class.getClassLoader().getResourceAsStream("help.txt")) {
            if (in != null) {
                int ch;
                while ((ch = in.read()) != -1) {
                    System.out.print((char) ch);
                }
            } else {
                System.out.println("Help file not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}