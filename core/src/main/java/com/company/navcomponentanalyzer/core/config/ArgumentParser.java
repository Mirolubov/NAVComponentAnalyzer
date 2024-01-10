package com.company.navcomponentanalyzer.core.config;

import java.util.ArrayList;
import java.util.List;

public class ArgumentParser {
    private final List<String> arguments;

    public ArgumentParser(String[] args) {
        this.arguments = List.of(args);
    }

    public boolean hasArguments() {
        return !arguments.isEmpty();
    }

    public boolean showHelp() {
        return arguments.contains("-?");
    }

    public boolean consoleMode() {
        return arguments.contains("-console");
    }

    public boolean containsArgument(String argument) {
        return arguments.contains(argument);
    }

    public String extractNextFolder() {
        int index = arguments.indexOf("-f");
        if (index != -1 && index < arguments.size() - 1) {
            return arguments.get(index + 1);
        }
        return "";
    }

    public String extractCharsetName() {
        int index = arguments.indexOf("-cs");
        if (index != -1 && index < arguments.size() - 1) {
            return arguments.get(index + 1);
        }
        return "";
    }

    public List<String> extractNextFiles() {
        List<String> files = new ArrayList<>();
        int index = arguments.indexOf("-n");
        if (index != -1) {
            for (int i = index + 1; i < arguments.size(); i++) {
                if (arguments.get(i).startsWith("-")) {
                    break;
                }
                files.add(arguments.get(i));
            }
        }
        return files;
    }
}
