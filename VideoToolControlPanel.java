import javax.swing.*;
import java.awt.*;

public class VideoToolControlPanel extends JPanel {
    //Font controlsFont = new Font("", Font.PLAIN, 16);
    //Font textFont = new Font("", Font.BOLD, 16);
    private SpringLayout layout;
    private JPanel actionSubPanel;
    private JPanel buttonSubPanel;
    private JPanel linkSubPanel;

    public VideoToolControlPanel() {
        this.actionSubPanel = new JPanel();
        this.buttonSubPanel = new JPanel();
        this.linkSubPanel = new JPanel();
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
        JLabel aspLabel = new JLabel("Action: ");

        // fill actionList
        String[] actions = {"Doctor", "Dinosaur", "Dinosaur 2"};
        JComboBox<String> actionList = new JComboBox<>(actions);

        linkSubPanel.add(aspLabel);
        linkSubPanel.add(actionList);

        this.add(linkSubPanel);
    }
}
