package com.company.view;
import com.company.config.AppProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent) {
        super(parent, "About", true);

        AppProperties prop = AppProperties.initAppProperties();
        String title = prop.getTitle();
        String version = prop.getVersion();
        String author = prop.getAuthor();
        String githubLink = prop.getGitHubLink();
        String email = prop.getEMail();
        String description= prop.getDescription();

        setSize(400, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel nameLabel = new JLabel(title);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descriptionLabel = new JLabel("Description: " + description);
        JLabel versionLabel = new JLabel("Version: " + version);
        JLabel authorLabel = new JLabel("Author: " + author);
        JLabel linkLabel = getLinkLabel("GitHub: ", githubLink);
        JLabel emailLabel = getLinkLabel("E-mail: ", email);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));
        panel.add(nameLabel);
        panel.add(descriptionLabel);
        panel.add(versionLabel);
        panel.add(authorLabel);
        panel.add(linkLabel);
        panel.add(emailLabel);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose()); // Close the dialog when the "Close" button is clicked

        add(panel, BorderLayout.CENTER);
        add(closeButton, BorderLayout.SOUTH);

        setLocationRelativeTo(parent); // Center the dialog on the parent frame
    }

    private static JLabel getLinkLabel(String labelCaption, String labelText) {
        JLabel linkLabel = new JLabel(labelCaption + labelText);
        linkLabel.setForeground(Color.BLUE);
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(labelText));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return linkLabel;
    }

}
