//Copyright 2022 Artiom Mirolyubov
//Licensed under the Apache License, Version 2.0

package com.company.navcomponentanalyzer.core;

import com.company.navcomponentanalyzer.core.config.AppProperties;
import com.company.navcomponentanalyzer.core.config.ArgumentParser;
import com.company.navcomponentanalyzer.core.model.NavObjects;
import com.company.navcomponentanalyzer.core.model.process.FileLoader;
import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;
import com.company.navcomponentanalyzer.core.model.search.PrintResult;
import com.company.navcomponentanalyzer.core.view.MainFrame;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main{

    public static void main(String[] args) {
        List<SearchProcessor> searchProcessors = loadSearchProcessors();

        boolean consoleMode = false;
        boolean consoleError = false;
        List<String> files = null;
        String folder = "";
        AppProperties appProperties = AppProperties.initAppProperties();
        ArgumentParser argumentParser = new ArgumentParser(args);
        if (argumentParser.hasArguments()) {
            if (argumentParser.showHelp()) {
                displayHelpMessage(searchProcessors);
            }
            consoleMode = argumentParser.consoleMode();
            folder = argumentParser.extractNextFolder();
            files = argumentParser.extractNextFiles();
            String charsetName = argumentParser.extractCharsetName();
            appProperties.setCharsetName(charsetName);
        }
        System.out.println("Charset: " + appProperties.getCharsetName());


        MainFrame mainFrame = null;
        NavObjects navObjects = new NavObjects();
        for (SearchProcessor searchProcessor : searchProcessors) {
            searchProcessor.setNavObjects(navObjects);
        }

        if(!consoleMode) {
            mainFrame = new MainFrame(navObjects, searchProcessors);
        }
        if (files != null) {
            for (String file : files) {
                FileLoader fileLoader = new FileLoader(String.format("%s/%s", folder, file), mainFrame, navObjects);
                fileLoader.processFile();
            }
            for (SearchProcessor searchProcessor : searchProcessors) {
                if(argumentParser.containsArgument(searchProcessor.getConsoleArgument()) || argumentParser.allPlugins()) {
                    consoleError |= PrintResult.print(searchProcessor.getCaption() + ": ", searchProcessor.search(""));
                }
            }
            if(consoleError){
                System.exit(3);
            }
        }
    }

    private static List<SearchProcessor> loadSearchProcessors() {
        Path pluginsDir = Paths.get("plugins");
        // Будем искать плагины в папке plugins
        ModuleFinder pluginsFinder = ModuleFinder.of(pluginsDir);

        // Пусть ModuleFinder найдёт все модули в папке plugins и вернёт нам список их имён
        List<String> plugins = pluginsFinder
                .findAll()
                .stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .filter(s -> !s.equals(Main.class.getModule().getName()))
                .collect(Collectors.toList());

        // Создадим конфигурацию, которая выполнит резолюцию указанных модулей (проверит корректность графа зависимостей)
        Configuration pluginsConfiguration = ModuleLayer
                .boot()
                .configuration()
                .resolve(pluginsFinder, ModuleFinder.of(), plugins);

        // Создадим слой модулей для плагинов
        ModuleLayer layer = ModuleLayer
                .boot()
                .defineModulesWithOneLoader(pluginsConfiguration, ClassLoader.getSystemClassLoader());

        // Найдём все реализации сервиса IService в слое плагинов и в слое Boot
        List<SearchProcessor> searchProcessors = SearchProcessor.getSearchProcessors(layer);
        return searchProcessors;
    }

    private static void displayHelpMessage(List<SearchProcessor> searchProcessors) {
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
        for (SearchProcessor searchProcessor : searchProcessors) {
            String description = searchProcessor.getDescription();
            if(description != null) {
                System.out.println(description);
            }
        }
    }
}