//Victor Zamarian
//Matas Empakeris
//CS 576

// This file sets up the GUI and the functionality for the hyperlinked video authoring tool.

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;


public class VideoTool {
    JFrame window;
    int width = 352;
    int height = 288;
    String primaryFilename;
    String secondaryFilename;

    private BufferedImage readFrame(String imageName){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        try {
            FileInputStream fileIn = new FileInputStream(imageName);
            DataInputStream dataIn = new DataInputStream(fileIn);

            byte[][] rData = new byte[width][height];
            byte[][] gData = new byte[width][height];
            byte[][] bData = new byte[width][height];


            //read image data
            for (int i = 0; i < 3; i++){
                for (int y = 0; y < height; y++){
                    for (int x = 0; x < width; x++){
                        byte value = dataIn.readByte();

                        if (i == 0){ //red data
                            rData[x][y] = value;
                        }else if (i == 1){ //green data
                            gData[x][y] = value;
                        }else{ //blue data
                            bData[x][y] = value;
                        }
                    }
                }
            }


            //create img
            for (int y = 0; y < height; y++){
                for (int x = 0; x < width; x++){
                    int pix = 0xff000000 | ((rData[x][y] & 0xff) << 16) | ((gData[x][y] & 0xff) << 8) | (bData[x][y] & 0xff);

                    img.setRGB(x, y, pix);
                }
            }


            dataIn.close();
        }catch(IOException e){
            System.out.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

        return img;
    }


    void displayGUI(){
        window = new JFrame("Hyperlinked Video Creator");
        window.setBounds(225, 60, width+width+60, height+380);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        Font controlsFont = new Font("", Font.PLAIN, 16);
        Font textFont = new Font("", Font.BOLD, 16);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.GRAY);

        //includes controls A, C, E, F
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBackground(Color.GRAY);
        controlPanel.setBounds(0, 0, window.getWidth(), 120);

        JPanel controlA = new JPanel();
        controlA.setLayout(null);
        controlA.setBackground(Color.GRAY);
        controlA.setBounds(20, 10, 270, 80); //probably wider

        JLabel controlALabel = new JLabel("Action : ");
        controlALabel.setBounds(2, 5, 70, 20);
        controlALabel.setFont(textFont);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(null);
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        actionsPanel.setBounds(70, 0, 200, 80);

        //for now this will be a text area, later make the text function like buttons (add mouse listeners)
        JTextArea actions = new JTextArea("Import Primary Video\nImport Secondary Video\nCreate new hyperlink");
        actions.setEditable(false);
        actions.setFont(controlsFont);
        actions.setBounds(10, 5, 180, 70);
        actionsPanel.add(actions);

        controlA.add(controlALabel);
        controlA.add(actionsPanel);

        controlPanel.add(controlA);


        //TODO 2: add controls C, E, F

        mainPanel.add(controlPanel);



        //includes displays for primary and secondary video frames and controls B, D
        JPanel framePanel = new JPanel();
        framePanel.setLayout(new GridBagLayout());
        framePanel.setBackground(Color.BLACK);
        framePanel.setBounds(0, 120, window.getWidth(), height+120);


        primaryFilename = "datasets/London/LondonOne/LondonOne.avi"; //temp, will get value from action A1
        primaryFilename = primaryFilename.substring(0, primaryFilename.indexOf('.'));

        secondaryFilename = "datasets/NewYorkCity/NYTwo/NYTwo.avi"; //temp, will get value from action A2
        secondaryFilename = secondaryFilename.substring(0, secondaryFilename.indexOf('.'));

        BufferedImage img1 = readFrame(primaryFilename+"0294.rgb");
        BufferedImage img2 = readFrame(secondaryFilename+"7562.rgb");



        JPanel primaryPanel = new JPanel();
        primaryPanel.setBackground(Color.BLUE);

        JLabel primaryFrame = new JLabel();
        primaryFrame.setIcon(new ImageIcon(img1));
        primaryPanel.add(primaryFrame);



        JPanel secondaryPanel = new JPanel();
        secondaryPanel.setBackground(Color.GREEN);

        JLabel secondaryFrame = new JLabel();
        secondaryFrame.setIcon(new ImageIcon(img2));
        secondaryPanel.add(secondaryFrame);


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0,10,0,15);
        framePanel.add(primaryPanel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0,0,0,20);
        framePanel.add(secondaryPanel, c);

        mainPanel.add(framePanel);

        window.getContentPane().add(mainPanel);
        window.setVisible(true);

    }


    public static void main(String args[]){
        VideoTool tool = new VideoTool();
        tool.displayGUI(); //this file doesn't/shouldn't depend on command line arguments
    }
}
