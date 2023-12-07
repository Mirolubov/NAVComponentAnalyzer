//Copyright 2022 Artiom Mirolyubov
//Licensed under the Apache License, Version 2.0

package com.company;

import com.company.config.ArgumentParser;
import com.company.model.NavObjects;
import com.company.model.process.FileLoader;
import com.company.model.search.PrintResult;
import com.company.model.search.SearchCaptionMLMiss;
import com.company.model.search.SearchProcessor;
import com.company.model.search.SearchTransactionInValidate;
import com.company.view.MainFrame;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main{

    public static void main(String[] args) {
        boolean consoleCaptionML = false;
        boolean consoleValidateTran = false;
        boolean consoleError = false;
        List<String> files = null;
        String folder = "";
        ArgumentParser argumentParser = new ArgumentParser(args);
        if (argumentParser.hasArguments()) {
            if (argumentParser.showHelp()) {
                displayHelpMessage();
            }
            folder = argumentParser.extractNextFolder();
            files = argumentParser.extractNextFiles();
            consoleCaptionML = argumentParser.consoleSearchCaptionML();
            consoleValidateTran = argumentParser.consoleValidateTran();
        }
        MainFrame mainFrame = null;
        NavObjects navObjects = new NavObjects();
        if(!consoleCaptionML && !consoleValidateTran) {
            mainFrame = new MainFrame(navObjects);
        }
        if (files != null) {
            for (String file : files) {
                FileLoader fileLoader = new FileLoader(String.format("%s/%s", folder, file), mainFrame, navObjects);
                fileLoader.processFile();
            }
            if(consoleCaptionML) {
                SearchProcessor searchProcessor = new SearchCaptionMLMiss(navObjects);
                consoleError |= PrintResult.print("No caption translation: ", searchProcessor.search(""));
            }
            if(consoleValidateTran) {
                SearchProcessor searchProcessor = new SearchTransactionInValidate(navObjects);
                consoleError |= PrintResult.print("New transaction in validate: ", searchProcessor.search(""));
            }
            if(consoleError){
                System.exit(3);
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