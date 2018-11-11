//Victor Zamarian
//Matas Empakeris
//CS 576

// This file sets up the GUI and the functionality for the hyperlinked video authoring tool.

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;


public class VideoTool extends JFrame {
    /*** display-related ***/
    private VideoToolControlPanel controlPanel;
    private VideoToolVideoPanel videoPanel;
    /*** end display-related ***/

    public VideoTool() {
        this.setTitle("Hyperlinked Video Creator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void displayGUI() {
        Container contentPane = this.getContentPane();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        contentPane.setLayout(gridbag);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Control sub-panel initialization
        controlPanel = new VideoToolControlPanel();
        controlPanel.initControlPanel();
        c.weightx = 1.0;
        c.weighty = 0.2;
        c.gridx = 0;
        c.gridy = 0;
        gridbag.setConstraints(controlPanel, c);
        contentPane.add(controlPanel);

        try {
            videoPanel = new VideoToolVideoPanel();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        videoPanel.initVideoPanel();
        c.weightx = 1.0;
        c.weighty = 0.8;
        c.gridx = 0;
        c.gridy = 1;
        gridbag.setConstraints(videoPanel, c);
        contentPane.add(videoPanel);

        // Add filler panel for bottom grey area
        JPanel filler = new JPanel();

        // Last display steps
        this.setSize(750, 500);
        this.setVisible(true);
    }

    public static void main(String args[]){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VideoTool tool = new VideoTool();
                tool.displayGUI(); //this file doesn't/shouldn't depend on command line arguments
            }
        });
    }
}
