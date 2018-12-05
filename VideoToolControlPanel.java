import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.FontUIResource;
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
    private JComboBox<String> linkModList;
    private VideoToolVideoPanel videoToolVideoPanel;
    private HyperlinkPanel hyperlinkPanel;
    private HyperlinkVideoPanel hyperlinkVideoPanel;
    private String currentHyperlinkPath;
    private boolean triggerListListener;
    private Font controlsFont = new Font("", Font.PLAIN, 16);


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
                if (hyperlinkPanel.isConnected(vtvp.getCurrentLink())) {
                    vtvp.setCurrentLink("");
                    enableModifyHyperlink(false);

                    hyperlinkPanel.revalidate();
                    hyperlinkPanel.repaint();
                    return;
                }

                // switching from unconnected link
                triggerListListener = false;

                if (cancelHyperlinkEdit()) {
                    hyperlinkPanel.removeLink(vtvp.getCurrentLink());
                    linkList.removeItem(vtvp.getCurrentLink());
                    vtvp.setCurrentLink("");
                    enableModifyHyperlink(false);
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
                enableModifyHyperlink(true);
                int primaryStartFrame = hyperlinkPanel.getHyperlink(vtvp.getCurrentLink()).getPrimaryStartFrame();
                VideoToolSliderPanel sliderA = vtvp.getSliderSubPanel('A');

                sliderA.getSliderFrameField().setText(String.format("%04d", primaryStartFrame));
                sliderA.getSlider().setValue(primaryStartFrame);

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
                enableModifyHyperlink(true);
                int primaryStartFrame = hyperlinkPanel.getHyperlink(vtvp.getCurrentLink()).getPrimaryStartFrame();
                VideoToolSliderPanel sliderA = vtvp.getSliderSubPanel('A');

                sliderA.getSliderFrameField().setText(String.format("%04d", primaryStartFrame));
                sliderA.getSlider().setValue(primaryStartFrame);
            }else{
                linkList.setSelectedItem(vtvp.getCurrentLink());
            }

            triggerListListener = true;
        }
    }

    private class LinkModListListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            VideoToolVideoPanel vtvp = VideoToolControlPanel.this.videoToolVideoPanel;
            JComboBox box = (JComboBox) e.getSource();

            if (box.getSelectedItem() == "Change Name"){
                String name = chooseLinkName();

                if (name == null){
                    return;
                }

                //a hyperlink must be active for the option to even be used
                triggerListListener = false;
                linkList.removeItem(vtvp.getCurrentLink());
                linkList.addItem(name);
                linkList.setSelectedItem(name);

                hyperlinkPanel.renameHyperlink(vtvp.getCurrentLink(), name);
                vtvp.setCurrentLink(name);
                triggerListListener = true;
            } else if (box.getSelectedItem() == "Change Color"){
                Color c = chooseLinkColor();

                if (c == null){
                    return;
                }

                hyperlinkPanel.changeLinkColor(vtvp.getCurrentLink(), c);
            } else if (box.getSelectedItem() == "Delete Link"){
                triggerListListener = false;

                hyperlinkPanel.removeLink(vtvp.getCurrentLink());
                linkList.removeItem(vtvp.getCurrentLink());
                linkList.setSelectedItem("-");
                vtvp.setCurrentLink("");
                enableModifyHyperlink(false);

                triggerListListener = true;
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
            chooser.setDialogTitle("Choose video directory");

            String startingDir = System.getProperty("user.dir");
            chooser.setCurrentDirectory(new File(startingDir));

            // have user choose video of allowed type or cancel
            //chooser.setFileFilter(new FileNameExtensionFilter(".avi", "avi"));
            JFrame window = (JFrame) SwingUtilities.getRoot(VideoToolControlPanel.this);
            VideoToolVideoPanel vtvp = VideoToolControlPanel.this.videoToolVideoPanel;

            VideoEditPanel vepB = VideoToolControlPanel.this.videoToolVideoPanel.getVideoSubPanel('B');
            VideoEditPanel vepA = VideoToolControlPanel.this.videoToolVideoPanel.getVideoSubPanel('A');

            boolean newLoad = false;

            if (box.getSelectedItem() == "Import Primary Video") {
                if (vepA.isVideoSet() && !hyperlinkPanel.isListEmpty()) {
                    JLabel message = new JLabel("Hyperlinks are being edited for this file, " +
                            "do you really want to switch to a new file?");
                    message.setFont(controlsFont);

                    if (JOptionPane.showConfirmDialog(window, message) == JOptionPane.OK_OPTION) {
                        message.setText("Do you want to save the current file?");

                        if (JOptionPane.showConfirmDialog(window, message) == JOptionPane.OK_OPTION){
                            currentHyperlinkPath = hyperlinkPanel.saveHyperlinks(vepA.getVideoName());
                        }

                        newLoad = true;
                    } else {
                        return;
                    }
                }
            }

            int value = chooser.showOpenDialog(window);
            if (value != JFileChooser.APPROVE_OPTION) {
                return;
            }

            // load video based on chosen option
            String filepath = chooser.getSelectedFile().getAbsolutePath();

            if (box.getSelectedItem() == "Import Primary Video") {
                if (vepA.loadVideo(filepath)) {
                    if (newLoad){ //only clear hyperlinks if a new primary video will actually be loaded
                        triggerListListener = false;

                        hyperlinkPanel.removeAllLinks();
                        linkList.removeAllItems();
                        linkList.addItem("-");
                        vtvp.setCurrentLink("");

                        triggerListListener = true;
                    }

                    VideoToolSliderPanel sliderA = vtvp.getSliderSubPanel('A');
                    sliderA.getSliderFrameField().setEnabled(true);
                    sliderA.getSlider().setEnabled(true);
                    sliderA.getSliderFrameField().setText(String.format("%04d", 1));
                    sliderA.getSlider().setValue(1);
                }
            } else if (box.getSelectedItem() == "Import Secondary Video") {
                if(vepB.loadVideo(filepath)) {
                    VideoToolSliderPanel sliderB = vtvp.getSliderSubPanel('B');
                    sliderB.getSliderFrameField().setEnabled(true);
                    sliderB.getSlider().setEnabled(true);
                    sliderB.getSliderFrameField().setText(String.format("%04d", 1));
                    sliderB.getSlider().setValue(1);
                }
            }
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            VideoEditPanel vep = videoToolVideoPanel.getVideoSubPanel('A');

            if (vep.isVideoSet()) { //at least the primary video needs to be loaded first
                currentHyperlinkPath = hyperlinkPanel.saveHyperlinks(vep.getVideoName());
            }
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
                (new VideoPlaybackTool(videoToPlayPath, START_FRAME, false, true)).displayGUI();
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

            triggerListListener = false;

            // connect hyperlink to secondary video
            if (!hyperlinkPanel.isConnected(currentLink)) {
                //int primaryStartFrame = hyperlinkPanel.getHyperlink(videoToolVideoPanel.getCurrentLink()).getPrimaryStartFrame();
                //int secondaryStartFrame = hyperlinkPanel.getHyperlink(videoToolVideoPanel.getCurrentLink()).getSecondaryStartFrame();
                int primaryEndFrame = videoToolVideoPanel.getVideoSubPanel('A').getCurrentFrameNumber();
                int secondaryStartFrame = videoToolVideoPanel.getVideoSubPanel('B').getCurrentFrameNumber();

                JFrame window = (JFrame) SwingUtilities.getRoot(VideoToolControlPanel.this);
                JLabel message = new JLabel("Hyperlink start frame cannot be less than the end frame!");
                message.setFont(controlsFont);

                if (hyperlinkPanel.setFrames(currentLink, primaryEndFrame, secondaryStartFrame) == 0) {
                    JOptionPane.showMessageDialog(window, message, "Error", JOptionPane.ERROR_MESSAGE);
                    triggerListListener = true;
                    return;
                }
            }

            // clear links
            videoToolVideoPanel.setCurrentLink("");
            enableModifyHyperlink(false);
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
        this.linkList = new JComboBox<>(new String[]{"-"});
        this.linkModList = new JComboBox<>(new String[]{"Change Name", "Change Color", "Delete Link"});
        this.videoToolVideoPanel = vtvp;
        this.triggerListListener = true;
        this.hyperlinkPanel = vtvp.getVideoSubPanel('A').getHyperlinkPanel();

        //all dialog buttons are larger
        UIManager.put("OptionPane.buttonFont", new FontUIResource(controlsFont));
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
        aspLabel.setFont(controlsFont);

        // fill actionList
        String[] actions = {"Import Primary Video", "Import Secondary Video", "Create new hyperlink"};
        JComboBox<String> actionList = new JComboBox<>(actions);
        actionList.addActionListener(new ActionButtonListener());
        actionList.setFont(controlsFont);

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

        connectButton.setFont(controlsFont);
        saveButton.setFont(controlsFont);
        playButton.setFont(controlsFont);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        buttonSubPanel.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.gridy = 0;
        buttonSubPanel.add(connectButton, c);

        c.gridx = 1;
        c.gridy = 0;
        buttonSubPanel.add(Box.createHorizontalStrut(10), c);

        c.gridx = 2;
        c.gridy = 0;
        buttonSubPanel.add(playButton, c);

        c.gridx = 0;
        c.gridy = 1;
        buttonSubPanel.add(Box.createVerticalStrut(10), c);

        c.gridx = 0;
        c.gridy = 2;
        buttonSubPanel.add(saveButton, c);

        //inner layout
        this.add(buttonSubPanel);
    }

    private void initLinkSubPanel() {
        // control B
        JLabel aspLabel = new JLabel("Link: ");
        aspLabel.setFont(controlsFont);

        linkList.addItemListener(new LinkListListener());
        linkList.setFont(controlsFont);
        linkList.setPreferredSize(new Dimension(120, 30));

        JLabel modLabel = new JLabel("Modify Link:");
        modLabel.setFont(controlsFont);

        linkModList.addActionListener(new LinkModListListener());
        linkModList.setFont(controlsFont);

        //add components to linkSubPanel
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        linkSubPanel.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.gridy = 0;
        linkSubPanel.add(aspLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        linkSubPanel.add(linkList, c);

        c.gridx = 0;
        c.gridy = 1;
        linkSubPanel.add(Box.createVerticalStrut(10), c);

        c.gridx = 0;
        c.gridy = 2;
        c.ipadx = 5;
        linkSubPanel.add(modLabel, c);

        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 0;
        linkSubPanel.add(Box.createVerticalStrut(10), c);

        c.gridx = 1;
        c.gridy = 2;
        linkSubPanel.add(linkModList, c);

        enableModifyHyperlink(false);

        this.add(linkSubPanel);
    }

    private boolean cancelHyperlinkEdit() {
        if (videoToolVideoPanel.getCurrentLink().equals("")) {
            return false;
        }

        JFrame window = (JFrame) SwingUtilities.getRoot(VideoToolControlPanel.this);
        JLabel message = new JLabel("Do you want to stop editing the current" +
                " hyperlink: " + videoToolVideoPanel.getCurrentLink() + "?");
        message.setFont(controlsFont);

        return (JOptionPane.showConfirmDialog(window, message) == JOptionPane.OK_OPTION);
    }

    private String chooseLinkName() {
        JFrame window = (JFrame) SwingUtilities.getRoot(VideoToolControlPanel.this);

        JLabel message = new JLabel("Enter a name for the hyperlink: ");
        message.setFont(controlsFont);

        String linkName = JOptionPane.showInputDialog(window, message);
        if (linkName == null) {
            return null;
        }

        //sanitize link name, moved from return statement because "" can be returned
        linkName = linkName.replaceAll("[^A-Za-z0-9 ]", "");

        if (linkName.equals("")) {

            message.setText("Invalid link name.");
            message.setFont(controlsFont);

            JOptionPane.showMessageDialog(window, message, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return linkName;
    }

    private Color chooseLinkColor() {
        JFrame window = (JFrame) SwingUtilities.getRoot(VideoToolControlPanel.this);

        JColorChooser colorPanel = new JColorChooser(Color.RED);
        for (AbstractColorChooserPanel p : colorPanel.getChooserPanels()){
            if (!p.getDisplayName().equals("Swatches")){
                colorPanel.removeChooserPanel(p);
            }
        }
        if (JOptionPane.showConfirmDialog(window, colorPanel, "Choose the box color",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null) == JOptionPane.OK_OPTION){
            return colorPanel.getColor();
        }

        return null;
    }

    private void enableModifyHyperlink(boolean enable) {
        linkModList.setEnabled(enable);
    }

    private void createHyperlink() {
        // error out if not all videos loaded
        VideoEditPanel vepA = videoToolVideoPanel.getVideoSubPanel('A');
        VideoEditPanel vepB = videoToolVideoPanel.getVideoSubPanel('B');

        if (!vepA.isVideoSet() || !vepB.isVideoSet()) {
            JFrame window = (JFrame) SwingUtilities.getRoot(VideoToolControlPanel.this);
            JLabel message = new JLabel("Need to load videos first.");
            message.setFont(controlsFont);

            JOptionPane.showMessageDialog(window, message, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Continue hyperlink creation or cancel if link is not in new state
        triggerListListener = false;
        String currentLink = videoToolVideoPanel.getCurrentLink();
        boolean currentLinkExists = !currentLink.equals("");
        boolean currentLinkConnected = hyperlinkPanel.isConnected(currentLink);

        if (currentLinkExists){
            if (!currentLinkConnected){
                if (!cancelHyperlinkEdit()){
                    triggerListListener = true;
                    return;
                }else{
                    linkList.removeItem(currentLink);
                    hyperlinkPanel.removeLink(currentLink);
                    videoToolVideoPanel.setCurrentLink("");
                    enableModifyHyperlink(false);
                }
            }
        }

        triggerListListener = true;

        // Finalize link settings and add
        String linkName = chooseLinkName();
        if (linkName == null) {
            return;
        }

        if (hyperlinkPanel.linkExists(linkName)){
            JFrame window = (JFrame) SwingUtilities.getRoot(VideoToolControlPanel.this);
            JLabel message = new JLabel("A link with the name \"" + linkName + "\" already exists");
            message.setFont(controlsFont);

            JOptionPane.showMessageDialog(window, message, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Color linkColor = chooseLinkColor();
        if (linkColor == null) {
            return;
        }

        triggerListListener = false;
        int primaryStartFrame = vepA.getCurrentFrameNumber();

        hyperlinkPanel.addHyperlink(linkName, vepA.getVideoPath(), vepB.getVideoPath(),
                primaryStartFrame, linkColor);
        linkList.addItem(linkName);
        linkList.setSelectedItem(linkName);
        videoToolVideoPanel.setCurrentLink(linkName);
        enableModifyHyperlink(true);
        triggerListListener = true;
    }
}
