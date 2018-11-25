import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class VideoToolVideoPanel extends JPanel {
    private GridBagConstraints c;
    private VideoEditPanel videoASubPanel;
    private VideoEditPanel videoBSubPanel;
    private VideoToolSliderPanel sliderASubPanel;
    private VideoToolSliderPanel sliderBSubPanel;
    private boolean textFieldChanged = false;

    // JTextField Listener
    private class TextFieldListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField currTextField = (JTextField) e.getSource();
            int textFieldValue;
            try {
                textFieldValue = Integer.parseInt(currTextField.getText());
            } catch (NumberFormatException err) {
                System.out.println("DEBUG: textFieldValue is not an integer");
                return;
            }

            textFieldChanged = true;
            if (currTextField.getParent() == sliderASubPanel) {
                sliderASubPanel.getSlider().setValue(textFieldValue);
                videoASubPanel.setFrame(textFieldValue);
                ((JLabel) videoASubPanel.getComponent(0)).setIcon(new ImageIcon(videoASubPanel.getFrame()));
            } else {
                sliderBSubPanel.getSlider().setValue(textFieldValue);
                videoBSubPanel.setFrame(textFieldValue);
                ((JLabel) videoBSubPanel.getComponent(0)).setIcon(new ImageIcon(videoBSubPanel.getFrame()));
            }

            currTextField.setText(String.format("%04d", textFieldValue));
            textFieldChanged = false;

            VideoToolVideoPanel.this.revalidate();
            VideoToolVideoPanel.this.repaint();
        }
    }

    // Slider Listener
    private class SliderListener implements ChangeListener {
        // TODO: Remove async ops if deemed unnecessary. Rasterizer renders this obsolete for now
        @Override
        public void stateChanged(ChangeEvent e) {
            if (textFieldChanged) {
                return;
            }

            final JSlider currSlider = (JSlider) e.getSource();
            if (currSlider.getParent() == sliderASubPanel) {
                sliderASubPanel.getSliderFrameField().setText(String.format("%04d", currSlider.getValue()));
            } else {
                sliderBSubPanel.getSliderFrameField().setText(String.format("%04d", currSlider.getValue()));
            }

            (new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    if (currSlider.getParent() == sliderASubPanel) {
                        videoASubPanel.setFrame(currSlider.getValue());
                    } else {
                        videoBSubPanel.setFrame(currSlider.getValue());
                    }

                    return null;
                }

                @Override
                public void done() {
                    if (currSlider.getParent() == sliderASubPanel) {
                        ((JLabel) videoASubPanel.getComponent(0)).setIcon(new ImageIcon(videoASubPanel.getFrame()));
                    } else {
                        ((JLabel) videoBSubPanel.getComponent(0)).setIcon(new ImageIcon(videoBSubPanel.getFrame()));
                    }
                }
            }).execute();

            VideoToolVideoPanel.this.revalidate();
            VideoToolVideoPanel.this.repaint();
        }
    }

    public VideoToolVideoPanel() {
        // Init video panel
        this.videoASubPanel = new VideoEditPanel('A', "London/LondonOne/LondonOne.avi");
        this.videoBSubPanel = new VideoEditPanel('B', "London/LondonOne/LondonOne.avi");

        // Init slider panel
        this.sliderASubPanel = new VideoToolSliderPanel('A');
        this.sliderBSubPanel = new VideoToolSliderPanel('B');
    }

    public VideoEditPanel getVideoSubPanel(char id) {
        return (id == 'A') ? this.videoASubPanel : this.videoBSubPanel;
    }

    public void initVideoPanel() {
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.BLACK);

        // layout
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 10;
        this.add(Box.createHorizontalBox(), c);
        c.ipady = 0;

        c.gridx = 0;
        c.gridy = 1;
        this.add(Box.createHorizontalStrut(5), c);

        initVideoSubPanel('A', 1, 1);

        c.gridx = 2;
        c.gridy = 1;
        this.add(Box.createHorizontalStrut(10), c);

        initVideoSubPanel('B', 3, 1);

        c.gridx = 4;
        c.gridy = 1;
        this.add(Box.createHorizontalStrut(5), c);

        c.gridx = 0;
        c.gridy = 2;
        c.ipady = 10;
        this.add(Box.createHorizontalBox(), c);
        c.ipady = 0;

        c.gridx = 0;
        c.gridy = 3;
        this.add(Box.createHorizontalStrut(5), c);

        initSliderSubPanel('A', 1, 3);

        c.gridx = 2;
        c.gridy = 3;
        this.add(Box.createHorizontalBox(), c);

        initSliderSubPanel('B', 3, 3);

        c.gridx = 4;
        c.gridy = 3;
        this.add(Box.createHorizontalStrut(5), c);

        c.gridx = 0;
        c.gridy = 4;
        c.ipady = 10;
        this.add(Box.createHorizontalGlue(), c);
        c.ipady = 0;
    }

    private void initVideoSubPanel(char id, int x, int y) {
        c.gridx = x;
        c.gridy = y;

        if (id == 'A') {
            JLabel img = new JLabel(new ImageIcon(videoASubPanel.getFrame()));
            videoASubPanel.add(img);
            videoASubPanel.setPreferredSize(new Dimension(352, 288));
            this.add(videoASubPanel, c);
        } else {
            JLabel img = new JLabel(new ImageIcon(videoBSubPanel.getFrame()));
            videoBSubPanel.add(img);
            videoBSubPanel.setPreferredSize(new Dimension(352, 288));
            this.add(videoBSubPanel, c);
        }
    }

    private void initSliderSubPanel(char id, int x, int y) {
        JSlider targetSlider = new JSlider(1, 9000, 1);
        targetSlider.addChangeListener(new SliderListener());

        JTextField targetField = new JTextField("0001");
        targetField.addActionListener(new TextFieldListener());

        c.gridx = x;
        c.gridy = y;

        if (id == 'A') {
            sliderASubPanel.setSlider(targetSlider);
            sliderASubPanel.setSliderFrameField(targetField);
            sliderASubPanel.add(targetField);
            sliderASubPanel.add(targetSlider);
            this.add(sliderASubPanel, c);
        } else {
            sliderBSubPanel.setSlider(targetSlider);
            sliderBSubPanel.setSliderFrameField(targetField);
            sliderBSubPanel.add(targetField);
            sliderBSubPanel.add(targetSlider);
            this.add(sliderBSubPanel, c);
        }
    }
}