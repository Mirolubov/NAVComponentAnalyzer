package com.company.navcomponentanalyzer.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {
    private static AppProperties appProperties;
    private final Properties properties;
    private String charsetName;

    private AppProperties() {
        properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.yml")) {
            properties.load(in);
            charsetName = properties.getProperty("charsetName");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AppProperties initAppProperties() {
        if (appProperties == null) {
            appProperties = new AppProperties();
        }
        return appProperties;
    }

    public String getTitle() {
        return properties.getProperty("title");
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getVersion() {
        return properties.getProperty("version");
    }

    public String getAuthor() {
        return properties.getProperty("author");
    }

    public String getGitHubLink() {
        return properties.getProperty("githubLink");
    }

    public String getEMail() {
        return properties.getProperty("e-mail");
    }

    public String getDescription() {
        return properties.getProperty("description");
    }

    public String getCaptionML() {
        return properties.getProperty("captionML");
    }
}
