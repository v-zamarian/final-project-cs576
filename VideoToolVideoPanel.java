import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class VideoToolVideoPanel extends JPanel {
    private GridBagConstraints c;
    private VideoEditPanel videoASubPanel;
    private VideoEditPanel videoBSubPanel;
    private JPanel sliderASubPanel;
    private JTextField sliderAFrameField;
    private JPanel sliderBSubPanel;
    private JTextField sliderBFrameField;

    // Slider Listener
    private class SliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider currSlider = (JSlider) e.getSource();
            sliderAFrameField.setText(Integer.toString(currSlider.getValue()));

            (new SwingWorker<Void, Void>() {
                @Override
                public Void doInBackground() {
                    videoASubPanel.setFrame(currSlider.getValue());
                    return null;
                }

                @Override
                public void done() {
                    ((JLabel) videoASubPanel.getComponent(0)).setIcon(new ImageIcon(videoASubPanel.getFrame()));
                }
            }).execute();

            VideoToolVideoPanel.this.revalidate();
            VideoToolVideoPanel.this.repaint();
        }
    }

    public VideoToolVideoPanel() throws IOException {
        this.videoASubPanel = new VideoEditPanel('A', "London/LondonOne/LondonOne.avi");
        this.videoBSubPanel = new VideoEditPanel('B', "London/LondonOne/LondonOne.avi");
        this.sliderAFrameField = new JTextField("1");
        this.sliderBFrameField = new JTextField("1");
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
        JLabel img = new JLabel(new ImageIcon(videoASubPanel.getFrame()));
        c.gridx = x;
        c.gridy = y;

        videoASubPanel.add(img);
        videoASubPanel.setPreferredSize(new Dimension(352, 288));
        this.add(videoASubPanel, c);
    }

    private void initSliderSubPanel(char id, int x, int y) {
        JSlider targetSlider = new JSlider(1, 9000, 1);
        targetSlider.addChangeListener(new SliderListener());
        JTextField frameField = new JTextField("1", 4);

        JPanel target = new JPanel();
        c.gridx = x;
        c.gridy = y;
        target.setPreferredSize(new Dimension(352, 50));
        sliderASubPanel = target;
        sliderASubPanel.setBackground(Color.GREEN);

        sliderASubPanel.add(frameField);
        sliderASubPanel.add(targetSlider);
        this.add(sliderASubPanel, c);
    }
}
