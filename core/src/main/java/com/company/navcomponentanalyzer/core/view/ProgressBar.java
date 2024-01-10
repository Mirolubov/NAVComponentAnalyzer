package com.company.navcomponentanalyzer.core.view;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JFrame implements Runnable {
    private final JProgressBar progressBar;

    public ProgressBar() {
        setTitle("Parsing...");
        setSize(300, 100);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.CENTER);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void run() {

    }
}
