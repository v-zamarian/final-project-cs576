//Victor Zamarian
//Matas Empakeris
//CS 576

// This file sets up the GUI and the functionality for the hyperlinked video authoring tool.

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class VideoTool {
    boolean debug = true;

    JFrame window;
    int width = 352;
    int height = 288;
    String primaryFilename = "";
    String secondaryFilename = "";
    String startingDir = System.getProperty("user.dir");

    JLabel primaryFrame;
    JLabel secondaryFrame;

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
            //System.exit(1);
            return null;
        }

        return img;
    }


    void displayGUI(){
        ToolTipManager.sharedInstance().setEnabled(false);
        //there was a really weired bug where when a tooltip was displayed, the window would
        //repaint over itself whenever the mouse was moved

        window = new JFrame("Hyperlinked Video Creator");
        window.setBounds(225, 60, width+width+60, height+350);
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
        controlPanel.setBounds(5, 5, window.getWidth(), 110);

        //control A, importing videos and creating hyperlinks
        JPanel controlA = new JPanel();
        controlA.setLayout(null);
        controlA.setBackground(Color.GRAY);
        controlA.setBounds(0, 10, 250, 90);

        JLabel controlALabel = new JLabel("Action :");
        controlALabel.setBounds(2, 5, 65, 20);
        controlALabel.setFont(textFont);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(null);
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        actionsPanel.setBounds(65, 0, 185, 90);

        Insets buttonInsets = new Insets(0, 0, 0, 0);

        final ControlAButton importPrimary = new ControlAButton("Import Primary Video");
        importPrimary.setFont(controlsFont);
        importPrimary.setMargin(buttonInsets);
        importPrimary.setBackground(Color.WHITE);
        importPrimary.setBounds(5, 5, 175, 25);

        final ControlAButton importSecondary = new ControlAButton("Import Secondary Video");
        importSecondary.setFont(controlsFont);
        importSecondary.setMargin(buttonInsets);
        importSecondary.setBackground(Color.WHITE);
        importSecondary.setBounds(5, 30, 175, 25);

        final ControlAButton createLink = new ControlAButton("Create new hyperlink");
        createLink.setFont(controlsFont);
        createLink.setMargin(buttonInsets);
        createLink.setBackground(Color.WHITE);
        createLink.setBounds(5, 55, 175, 25);

        //add action listeners for A1,2,3 here
        //button listener for control A (opening files and creating hyperlink)
        class ControlAListener implements ActionListener{
            JFileChooser chooser = new JFileChooser(startingDir);

            ControlAListener(){
                chooser.setFileFilter(new FileNameExtensionFilter("*.avi", "avi"));
            }

            @Override
            public void actionPerformed(ActionEvent e){
                if (e.getSource() == importPrimary){
                    int value = chooser.showOpenDialog(null);

                    if (value == JFileChooser.APPROVE_OPTION){
                        primaryFilename = chooser.getSelectedFile().getAbsolutePath();
                        primaryFilename = primaryFilename.substring(0, primaryFilename.indexOf('.'));

                        loadVideo(0);

                        chooser.setCurrentDirectory(new File(startingDir));
                    }
                }else if (e.getSource() == importSecondary){
                    int value = chooser.showOpenDialog(null);

                    if (value == JFileChooser.APPROVE_OPTION){
                        secondaryFilename = chooser.getSelectedFile().getAbsolutePath();
                        secondaryFilename = secondaryFilename.substring(0, secondaryFilename.indexOf('.'));

                        loadVideo(1);

                        chooser.setCurrentDirectory(new File(startingDir));
                    }
                }else{ //create hyperlink
                    if (debug) {
                        System.out.println("Creating hyperlink " + primaryFilename);
                    }
                }
            }
        }

        ControlAListener controlAListen = new ControlAListener();

        importPrimary.addActionListener(controlAListen);
        importSecondary.addActionListener(controlAListen);
        createLink.addActionListener(controlAListen);

        actionsPanel.add(importPrimary);
        actionsPanel.add(importSecondary);
        actionsPanel.add(createLink);

        controlA.add(controlALabel);
        controlA.add(actionsPanel);

        controlPanel.add(controlA);


        //control C, selecting hyperlinks
        JPanel controlC = new JPanel();
        controlC.setLayout(null);
        controlC.setBackground(Color.GRAY);
        controlC.setBounds(controlA.getWidth()+20, 10, 250, 80);

        JLabel controlCLabel = new JLabel("Select Link :");
        controlCLabel.setBounds(2, 5, 100, 20);
        controlCLabel.setFont(textFont);

        JPanel linkPanel = new JPanel();
        linkPanel.setLayout(null);
        linkPanel.setBackground(Color.WHITE);
        linkPanel.setBounds(100, 0, 150, 80);

        JTextArea hyperlinks = new JTextArea("Flowers 1\nFlowers 2\nGarden\nFlowers 3\nFlag");
        hyperlinks.setEditable(false);
        hyperlinks.setFont(controlsFont);
        hyperlinks.setMargin(new Insets(2, 5, 2, 0));

        JScrollPane hyperlinksScroll = new JScrollPane(hyperlinks);
        hyperlinksScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        hyperlinksScroll.setBounds(0, 0, 150, 80);
        hyperlinksScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        linkPanel.add(hyperlinksScroll);

        controlC.add(linkPanel);
        controlC.add(controlCLabel);

        controlPanel.add(controlC);


        //control E, connect videos via hyperlink
        JButton controlE = new JButton("<html>Connect Video</html>");
        controlE.setFont(controlsFont);
        controlE.setMargin(new Insets(2, 2, 2, 2));
        controlE.setBounds(controlC.getX()+controlC.getWidth()+30, 15, 80, 70);

        controlPanel.add(controlE);


        //control F, save meta data file
        JButton controlF = new JButton("<html>Save File</html>");
        controlF.setFont(controlsFont);
        controlF.setMargin(buttonInsets);
        controlF.setBounds(controlE.getX()+100, 15, 80, 70);

        controlPanel.add(controlF);
        mainPanel.add(controlPanel);


        //includes displays for primary and secondary video frames and controls B, D
        JPanel framePanel = new JPanel();
        framePanel.setLayout(null);
        framePanel.setBackground(Color.BLACK);
        framePanel.setBounds(0, 120, window.getWidth(), height+120);

        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout(null);
        primaryPanel.setBackground(Color.BLACK);
        primaryPanel.setBounds(15, 0, width, height+120);

        JPanel primaryFramePanel = new JPanel();
        primaryFramePanel.setLayout(null);
        primaryFramePanel.setBackground(Color.LIGHT_GRAY);
        primaryFramePanel.setBounds(0, 20, width, height);

        primaryFrame = new JLabel();
        primaryFrame.setBounds(0, 0, width, height);
        primaryFramePanel.add(primaryFrame);
        primaryPanel.add(primaryFramePanel);


        //control B
        JPanel controlB = new JPanel();
        controlB.setLayout(null);
        controlB.setBackground(Color.WHITE);
        controlB.setBounds(0, height+40, width, 60);

        JButton minus100P = new JButton("-100");
        minus100P.setBounds(4, 10, 44, 40);
        minus100P.setMargin(buttonInsets);
        minus100P.setFont(controlsFont);

        JButton minus10P = new JButton("-10");
        minus10P.setBounds(52, 10, 44, 40);
        minus10P.setMargin(buttonInsets);
        minus10P.setFont(controlsFont);

        JButton minus1P = new JButton("-1");
        minus1P.setBounds(100, 10, 44, 40);
        minus1P.setMargin(buttonInsets);
        minus1P.setFont(controlsFont);

        JTextField frameP = new JTextField("1");
        frameP.setBounds(148, 10, 56, 40);
        frameP.setHorizontalAlignment(SwingConstants.CENTER);
        frameP.setFont(controlsFont);

        JButton plus1P = new JButton("+1");
        plus1P.setBounds(208, 10, 44, 40);
        plus1P.setMargin(buttonInsets);
        plus1P.setFont(controlsFont);

        JButton plus10P = new JButton("+10");
        plus10P.setBounds(256, 10, 44, 40);
        plus10P.setMargin(buttonInsets);
        plus10P.setFont(controlsFont);

        JButton plus100P = new JButton("+100");
        plus100P.setBounds(304, 10, 44, 40);
        plus100P.setMargin(buttonInsets);
        plus100P.setFont(controlsFont);

        //TODO: add button listeners here
        //also add listener for changing the frame number

        controlB.add(minus100P);
        controlB.add(minus10P);
        controlB.add(minus1P);
        controlB.add(frameP);
        controlB.add(plus1P);
        controlB.add(plus10P);
        controlB.add(plus100P);

        primaryPanel.add(controlB);



        JPanel secondaryPanel = new JPanel();
        secondaryPanel.setLayout(null);
        secondaryPanel.setBackground(Color.BLACK);
        secondaryPanel.setBounds(window.getWidth()-width-20, 0, width, height+120);

        JPanel secondaryFramePanel = new JPanel();
        secondaryFramePanel.setLayout(null);
        secondaryFramePanel.setBackground(Color.LIGHT_GRAY);
        secondaryFramePanel.setBounds(0, 20, width, height);

        secondaryFrame = new JLabel();
        secondaryFrame.setBounds(0, 0, width, height);
        secondaryFramePanel.add(secondaryFrame);
        secondaryPanel.add(secondaryFramePanel);

        //control D
        JPanel controlD = new JPanel();
        controlD.setLayout(null);
        controlD.setBackground(Color.WHITE);
        controlD.setBounds(0, height+40, width, 60);

        JButton minus100S = new JButton("-100");
        minus100S.setBounds(4, 10, 44, 40);
        minus100S.setMargin(buttonInsets);
        minus100S.setFont(controlsFont);

        JButton minus10S = new JButton("-10");
        minus10S.setBounds(52, 10, 44, 40);
        minus10S.setMargin(buttonInsets);
        minus10S.setFont(controlsFont);

        JButton minus1S = new JButton("-1");
        minus1S.setBounds(100, 10, 44, 40);
        minus1S.setMargin(buttonInsets);
        minus1S.setFont(controlsFont);

        JTextField frameS = new JTextField("1");
        frameS.setBounds(148, 10, 56, 40);
        frameS.setHorizontalAlignment(SwingConstants.CENTER);
        frameS.setFont(controlsFont);

        JButton plus1S = new JButton("+1");
        plus1S.setBounds(208, 10, 44, 40);
        plus1S.setMargin(buttonInsets);
        plus1S.setFont(controlsFont);

        JButton plus10S = new JButton("+10");
        plus10S.setBounds(256, 10, 44, 40);
        plus10S.setMargin(buttonInsets);
        plus10S.setFont(controlsFont);

        JButton plus100S = new JButton("+100");
        plus100S.setBounds(304, 10, 44, 40);
        plus100S.setMargin(buttonInsets);
        plus100S.setFont(controlsFont);

        //TODO: add button listeners here
        //also add listener for changing the frame number

        controlD.add(minus100S);
        controlD.add(minus10S);
        controlD.add(minus1S);
        controlD.add(frameS);
        controlD.add(plus1S);
        controlD.add(plus10S);
        controlD.add(plus100S);

        secondaryPanel.add(controlD);



        framePanel.add(primaryPanel);
        framePanel.add(secondaryPanel);

        mainPanel.add(framePanel);

        window.getContentPane().add(mainPanel);
        window.setVisible(true);
    }

    //for now this just loads the first frame of the selected video
    public void loadVideo(int type){
        String fileName;

        if (type == 0){
            fileName = primaryFilename;
        }else{
            fileName = secondaryFilename;
        }

        BufferedImage img = readFrame(fileName + "0001.rgb");

        if (img != null){
            if (debug){
                if (type == 0) {
                    System.out.print("Importing primary");
                }else{
                    System.out.print("Importing secondary");
                }

                System.out.println(" video: " + fileName + ".avi");
            }

            if (type == 0) {
                primaryFrame.setIcon(new ImageIcon(img));
            }else {
                secondaryFrame.setIcon(new ImageIcon(img));
            }
        }
    }


    //custom button for control A buttons
    class ControlAButton extends JButton{
        Color hoverColor = Color.BLACK;
        Color pressedColor = Color.LIGHT_GRAY;

        public ControlAButton(String text){
            super(text);
            super.setContentAreaFilled(false);
            super.setBorderPainted(false);
            super.setFocusPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g){
            if (getModel().isPressed()) {
                g.setColor(pressedColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            } else if (getModel().isRollover()) {
                g.setColor(hoverColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(getBackground());
                g.fillRect(2, 2, getWidth()-4, getHeight()-4);
            } else {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            super.paintComponent(g);
        }
    }


    public static void main(String args[]){
        VideoTool tool = new VideoTool();
        tool.displayGUI(); //this file doesn't/shouldn't depend on command line arguments
    }
}
