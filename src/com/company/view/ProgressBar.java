package com.company.view;

import javax.swing.*;
import java.awt.*;

public class ProgressBar extends JFrame implements Runnable {
    private final JProgressBar progressBar;

    public ProgressBar(int maxBorder, MainFrame mainFrame) {
        setTitle("Parsing...");
        setSize(300, 100);
        setLocationRelativeTo(mainFrame);

        progressBar = new JProgressBar(0, maxBorder);
        progressBar.setStringPainted(true);
        setLocationRelativeTo(mainFrame);
        add(progressBar, BorderLayout.CENTER);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void run() {

    }
}
