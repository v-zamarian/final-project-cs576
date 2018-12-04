import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

public class VideoToolControlPanel extends JPanel {
    private JPanel actionSubPanel;
    private JPanel buttonSubPanel;
    private JPanel linkSubPanel;
    private JComboBox<String> linkList;
    private VideoToolVideoPanel videoToolVideoPanel;
    private HyperlinkPanel hyperlinkPanel;
    private HyperlinkVideoPanel hyperlinkVideoPanel;
    private String currentHyperlinkPath;
    private boolean triggerListListener;


    private class LinkListListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            VideoToolVideoPanel vtvp = VideoToolControlPanel.this.videoToolVideoPanel;

            if (!triggerListListener || e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            // switching to no active link
            if (e.getItem().equals("-")) {

                // switching from connected link
                if (hyperlinkPanel.isConnected(vtvp.getName())) {
                    vtvp.setCurrentLink("");
                    VideoToolControlPanel.this.enableHyperlinkButtons(false);

                    hyperlinkPanel.revalidate();
                    hyperlinkPanel.repaint();
                    return;
                }

                // switching from unconnected link
                triggerListListener = false;
                if (VideoToolControlPanel.this.cancelHyperlinkEdit()) {
                    hyperlinkPanel.removeLink(vtvp.getCurrentLink());
                    linkList.removeItem(vtvp.getCurrentLink());
                    vtvp.setCurrentLink("");
                    enableHyperlinkButtons(false);
                } else {
                    linkList.setSelectedItem(vtvp.getCurrentLink());
                }
                triggerListListener = true;

                hyperlinkPanel.revalidate();
                hyperlinkPanel.repaint();
                return;
            }

            // if selected link is not the current link, change it to be the current link
            if (vtvp.getCurrentLink().equals("") || hyperlinkPanel.isConnected(vtvp.getCurrentLink())) {
                vtvp.setCurrentLink((String) e.getItem());
                enableHyperlinkButtons(true);
                int primaryStartFrame = hyperlinkPanel.getHyperlink(vtvp.getCurrentLink()).getPrimaryStartFrame();
                vtvp.getSliderSubPanel('A')
                        .getSliderFrameField()
                        .setText(Integer.toString(primaryStartFrame));

                hyperlinkPanel.revalidate();
                hyperlinkPanel.repaint();
                return;
            }

            // same as above but for an unconnected link
            triggerListListener = false;
            if (cancelHyperlinkEdit()) {
                hyperlinkPanel.removeLink(vtvp.getCurrentLink());
                linkList.removeItem(vtvp.getCurrentLink());

                vtvp.setCurrentLink((String) e.getItem());
                enableHyperlinkButtons(true);
                int primaryStartFrame = hyperlinkPanel.getHyperlink(vtvp.getCurrentLink()).getPrimaryStartFrame();
                vtvp.getSliderSubPanel('A')
                        .getSliderFrameField()
                        .setText(Integer.toString(primaryStartFrame));
            }

        }
    }

    private class ActionButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox box = (JComboBox) e.getSource();

            // No need to open video if all we want is a hyperlink connection
            if (box.getSelectedItem() == "Create new hyperlink") {
                createHyperlink();
                return;
            }

            // set current directory to user's system's directory
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            String startingDir = System.getProperty("user.dir");
            chooser.setCurrentDirectory(new File(startingDir));

            // have user choose video of allowed type or cancel
            //chooser.setFileFilter(new FileNameExtensionFilter(".avi", "avi"));
            int value = chooser.showOpenDialog(null);
            if (value != JFileChooser.APPROVE_OPTION) {
                return;
            }

            // load video based on chosen option
            VideoEditPanel vepA = VideoToolControlPanel.this.videoToolVideoPanel.getVideoSubPanel('A');
            VideoEditPanel vepB = VideoToolControlPanel.this.videoToolVideoPanel.getVideoSubPanel('B');
            String filepath = chooser.getSelectedFile().getAbsolutePath();

            if (box.getSelectedItem() == "Import Primary Video") {
                vepA.loadVideo(filepath);
            } else if (box.getSelectedItem() == "Import Secondary Video") {
                vepB.loadVideo(filepath);
            }
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentHyperlinkPath = hyperlinkPanel.saveHyperlinks(videoToolVideoPanel.getVideoSubPanel('A').getVideoName());
        }
    }

    private class PlayListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            VideoEditPanel vepA = VideoToolControlPanel.this.videoToolVideoPanel.getVideoSubPanel('A');
            String videoToPlayPath = vepA.getVideoPath();

            try {
                final int START_FRAME = 1;
                System.out.println("VIDEO TO PLAY: " + videoToPlayPath);
                (new VideoPlaybackTool(videoToPlayPath, START_FRAME, false)).displayGUI();
            } catch (Exception ex) {
                System.out.println("DEBUG: User cancelled operation");
            }
        }
    }

    private class ConnectHyperlinkListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String currentLink = videoToolVideoPanel.getCurrentLink();

            if (currentLink.equals("")) {
                return;
            }

            triggerListListener = true;

            // connect hyperlink to secondary video
            if (!hyperlinkPanel.isConnected(videoToolVideoPanel.getCurrentLink())) {
                //int primaryStartFrame = hyperlinkPanel.getHyperlink(videoToolVideoPanel.getCurrentLink()).getPrimaryStartFrame();
                //int secondaryStartFrame = hyperlinkPanel.getHyperlink(videoToolVideoPanel.getCurrentLink()).getSecondaryStartFrame();
                int primaryEndFrame = videoToolVideoPanel.getVideoSubPanel('A').getCurrentFrameNumber();
                int secondaryStartFrame = videoToolVideoPanel.getVideoSubPanel('B').getCurrentFrameNumber();

                JFrame window = (JFrame) SwingUtilities.getRoot(VideoToolControlPanel.this);

                if (hyperlinkPanel.setFrames(currentLink, primaryEndFrame, secondaryStartFrame) == 0) {
                    JOptionPane.showMessageDialog(window, "Hyperlink start frame cannot be less than the end frame!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    triggerListListener = true;
                    return;
                }
            }

            // clear links
            videoToolVideoPanel.setCurrentLink("");
            enableHyperlinkButtons(false);
            linkList.setSelectedItem("-");

            hyperlinkPanel.revalidate();
            hyperlinkPanel.repaint();

            triggerListListener = true;

        }
    }

    public VideoToolControlPanel(VideoToolVideoPanel vtvp) {
        this.actionSubPanel = new JPanel();
        this.buttonSubPanel = new JPanel();
        this.linkSubPanel = new JPanel();
        this.linkList = new JComboBox<>();
        this.videoToolVideoPanel = vtvp;
        this.triggerListListener = false;
        this.hyperlinkPanel = vtvp.getVideoSubPanel('A').getHyperlinkPanel();
    }

    public void initControlPanel() {
        initActionSubPanel();
        initLinkSubPanel();
        initButtonSubPanel();
    }

    private void initActionSubPanel() {
        // control A
        // label
        JLabel aspLabel = new JLabel("Action: ");

        // fill actionList
        String[] actions = {"Import Primary Video", "Import Secondary Video", "Create new hyperlink"};
        JComboBox<String> actionList = new JComboBox<>(actions);
        actionList.addActionListener(new ActionButtonListener());

        actionSubPanel.add(aspLabel);
        actionSubPanel.add(actionList);

        this.add(actionSubPanel);
    }

    private void initButtonSubPanel() {
        // control C
        JButton connectButton = new JButton("Connect Video");
        JButton saveButton = new JButton("Save File");
        JButton playButton = new JButton("Play!");
        connectButton.addActionListener(new ConnectHyperlinkListener());
        saveButton.addActionListener(new SaveButtonListener());
        playButton.addActionListener(new PlayListener());

        buttonSubPanel.add(connectButton);
        buttonSubPanel.add(saveButton);
        buttonSubPanel.add(playButton);

        //inner layout
        this.add(buttonSubPanel);
    }

    private void initLinkSubPanel() {
        // control B
        JLabel aspLabel = new JLabel("Link: ");

        linkList.addItemListener(new LinkListListener());

        linkSubPanel.add(aspLabel);
        linkSubPanel.add(linkList);

        this.add(linkSubPanel);
    }

    private boolean cancelHyperlinkEdit() {
        if (videoToolVideoPanel.getCurrentLink().equals("")) {
            return false;
        }

        //error shows up saying cannot cast JPanel to JFrame
        //JFrame window = (JFrame) VideoToolControlPanel.this.getParent();
        return (JOptionPane.showConfirmDialog(VideoToolControlPanel.this.getParent(),
                "Do you want to stop editing the current" +
                " hyperlink: " + videoToolVideoPanel.getCurrentLink() + "?") == JOptionPane.OK_OPTION);
    }

    private String chooseLinkName() {
        String linkName = JOptionPane.showInputDialog("Enter a name for the hyperlink: ");
        if (linkName == null) {
            return null;
        } else if (linkName.equals("")) {
            //JFrame window = (JFrame) VideoToolControlPanel.this.getParent();
            JOptionPane.showMessageDialog(VideoToolControlPanel.this.getParent(),
                    "Invalid link name.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // sanitize link name and return
        return linkName.replaceAll("[^A-Za-z0-9 ]", "");
    }

    private Color chooseLinkColor() {
        JColorChooser colorPanel = new JColorChooser(Color.RED);
        for (AbstractColorChooserPanel p : colorPanel.getChooserPanels()){
            if (!p.getDisplayName().equals("Swatches")){
                colorPanel.removeChooserPanel(p);
            }
        }
        if (JOptionPane.showConfirmDialog(null, colorPanel, "Choose the box color",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null) == JOptionPane.OK_OPTION){
            return colorPanel.getColor();
        }

        return null;
    }

    private void enableHyperlinkButtons(boolean enable) {
        for (Component linkSubPanelItem : linkSubPanel.getComponents()) {
            linkSubPanelItem.setEnabled(enable);
        }
    }

    private void createHyperlink() {
        // error out if not all videos loaded
        VideoEditPanel vepA = videoToolVideoPanel.getVideoSubPanel('A');
        VideoEditPanel vepB = videoToolVideoPanel.getVideoSubPanel('B');
        if (!vepA.isVideoSet() || !vepB.isVideoSet()) {
            //JFrame window = (JFrame) VideoToolControlPanel.this.getParent();
            JOptionPane.showMessageDialog(VideoToolControlPanel.this.getParent(),
                    "Need to load videos first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Continue hyperlink creation or cancel if link is not in new state
        String currentLink = videoToolVideoPanel.getCurrentLink();
        boolean currentLinkExists = !currentLink.equals("");
        boolean currentLinkConnected = hyperlinkPanel.isConnected(currentLink);
        if (currentLinkExists && !currentLinkConnected && !cancelHyperlinkEdit()) {
            triggerListListener = true;
            return;
        } else {
            linkList.removeItem(currentLink);
            hyperlinkPanel.removeLink(currentLink);
            videoToolVideoPanel.setCurrentLink("");
            enableHyperlinkButtons(false);
        }

        // Finalize link settings and add
        String linkName = chooseLinkName();
        if (linkName == null) {
            return;
        }
        Color linkColor = chooseLinkColor();
        if (linkColor == null) {
            return;
        }
        int primaryStartFrame = videoToolVideoPanel.getVideoSubPanel('A').getCurrentFrameNumber();
        hyperlinkPanel.addHyperlink(linkName, vepA.getVideoPath(), vepB.getVideoPath(),
                primaryStartFrame, linkColor);
        linkList.addItem(linkName);
        linkList.setSelectedItem(linkName);
        videoToolVideoPanel.setCurrentLink(linkName);
        enableHyperlinkButtons(true);
    }
}
