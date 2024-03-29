package com.company.navcomponentanalyzer.core.model.process;

import com.company.navcomponentanalyzer.core.config.AppProperties;
import com.company.navcomponentanalyzer.core.model.object.NavObject;
import com.company.navcomponentanalyzer.core.model.NavObjects;
import com.company.navcomponentanalyzer.core.model.object.NavType;
import com.company.navcomponentanalyzer.core.view.MainFrame;
import com.company.navcomponentanalyzer.core.view.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

public class ProcessFile implements Runnable{
    private NavObjects navObjects;
    private final StringBuilder bodyBuilder;
    private int id;
    private String name;
    private String strNavType;
    private final BlockingQueue<NavObject> navQueue;
    private File file;
    private final MainFrame mainFrame;
    private ProgressBar progressBar;
    private int values;

    @Override
    public void run() {
        try {
            if(mainFrame != null) {
                progressBar = new ProgressBar();
                progressBar.setLocationRelativeTo(mainFrame);
                progressBar.setVisible(true);
                Thread progressBarThread = new Thread(progressBar);
                progressBarThread.start();
            }
            values = 0;
            AppProperties appProperties = AppProperties.initAppProperties();
            String charsetName = appProperties.getCharsetName();
            BufferedReader reader = new BufferedReader(new FileReader(file, Charset.forName(charsetName)));
            String line;
            while ((line = reader.readLine()) != null) {
                fillNavObjectList(line);
            }
            reader.close();
            NavObject nullObject = new NavObject(0, "", NavType.Table);
            navQueue.put(nullObject);

            if(mainFrame != null) {
                mainFrame.updateTree();
                progressBar.setVisible(false);
                progressBar.dispose();
            }
        }catch  (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public ProcessFile(BlockingQueue<NavObject> navQueue, File file, MainFrame mainFrame, NavObjects navObjects) {
        bodyBuilder = new StringBuilder();
        clear();
        this.navQueue = navQueue;
        this.file = file;
        this.mainFrame = mainFrame;
        this.navObjects = navObjects;
    }

    private void fillNavObjectList(String line) throws InterruptedException {
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
            values ++;
            if(progressBar != null) {
                progressBar.getProgressBar().setValue(values);
            }
            synchronized (navObjects) {
                NavObject navObject = navObjects.add(id, name, strNavType, bodyBuilder.toString());
                if(navObject != null) {
                    navQueue.put(navObject);
                }
            }
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

