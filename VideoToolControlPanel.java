import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class VideoToolControlPanel extends JPanel {
    private JPanel actionSubPanel;
    private JPanel buttonSubPanel;
    private JPanel linkSubPanel;
    private VideoEditPanel videoEditPanelA;
    private VideoEditPanel videoEditPanelB;

    //TODO: ack v-zamarian logic
    private class ActionButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JComboBox box = (JComboBox) e.getSource();

            // set current directory to user's system's directory
            JFileChooser chooser = new JFileChooser();
            String startingDir = System.getProperty("user.dir");
            chooser.setCurrentDirectory(new File(startingDir));

            // have user choose video of allowed type or cancel
            chooser.setFileFilter(new FileNameExtensionFilter("avi or wav only",
                                                             "avi", "wav"));
            int value = chooser.showOpenDialog(null);
            if (value != JFileChooser.APPROVE_OPTION) {
                return;
            }

            // load video based on chosen option
            String filepath = chooser.getSelectedFile().getAbsolutePath();
            if (box.getSelectedItem() == "Import Primary Video") {
                VideoToolControlPanel.this.videoEditPanelA.loadVideo(filepath);
            } else if (box.getSelectedItem() == "Import Secondary Video") {
                VideoToolControlPanel.this.videoEditPanelB.loadVideo(filepath);
            }
        }
    }

    public VideoToolControlPanel(VideoEditPanel vepa, VideoEditPanel vepb) {
        this.actionSubPanel = new JPanel();
        this.buttonSubPanel = new JPanel();
        this.linkSubPanel = new JPanel();
        this.videoEditPanelA = vepa;
        this.videoEditPanelB = vepb;
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

        buttonSubPanel.add(connectButton);
        buttonSubPanel.add(saveButton);

        //inner layout
        this.add(buttonSubPanel);
    }

    private void initLinkSubPanel() {
        // control A
        // label
        JLabel aspLabel = new JLabel("Link: ");

        // fill actionList
        String[] links = {"Doctor", "Dinosaur", "Dinosaur 2"};
        JComboBox<String> linkList = new JComboBox<>(links);

        linkSubPanel.add(aspLabel);
        linkSubPanel.add(linkList);

        this.add(linkSubPanel);
    }
}
