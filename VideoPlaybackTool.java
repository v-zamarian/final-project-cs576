import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VideoPlaybackTool extends JFrame {
    private Video video;
    private AudioWrapper audio;
    private VideoPlaybackControlPanel controlPanel;
    private JLabel videoPanel;
    private JPanel infoPanel;
    private Timer playbackTimer;
    private int currentFrameNumber;

    final private int FRAMES_PER_SECOND = 31;
    final private int TOTAL_FRAMES = 9000;
    final private int DELAY_THRESHOLD = 5;

    private class PlaybackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton pressed = (JButton) e.getSource();
            if (pressed == VideoPlaybackTool.this.controlPanel.getPlay()) {
                VideoPlaybackTool.this.play();
            } else if (pressed == VideoPlaybackTool.this.controlPanel.getPause()) {
                VideoPlaybackTool.this.pause();
            } else if (pressed == VideoPlaybackTool.this.controlPanel.getStop()) {
                VideoPlaybackTool.this.stop();
            }
        }
    }

    private class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentFrameNumber == TOTAL_FRAMES) {
                resetFrames();
                revalidate();
                repaint();

                return;
            }

            videoPanel.setIcon(new ImageIcon(video.getFrame(currentFrameNumber)));
            ((JLabel) infoPanel.getComponent(0)).setText("Playing frame " + currentFrameNumber);
//            if (Math.abs((1470*currentFrameNumber) - audio.getAudio().getFramePosition()) > DELAY_THRESHOLD) {
//                audio.getAudio().setFramePosition(1470*currentFrameNumber);
//            }

            revalidate();
            repaint();
            currentFrameNumber++;
        }
    }

    public VideoPlaybackTool(String filepath) {
        this.setTitle("Hyperlinked Video Player");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.video = new Video(filepath);

        // init AudioWrapper object
        int p = filepath.lastIndexOf(".");
        String basePathName = filepath.substring(0, p);
        this.audio = new AudioWrapper(basePathName);

        this.controlPanel = new VideoPlaybackControlPanel();
        this.videoPanel = new JLabel(new ImageIcon(this.video.getFrame(1)));
        this.infoPanel = new JPanel();

        this.playbackTimer = new Timer(1000/FRAMES_PER_SECOND, new TimerListener());
        this.playbackTimer.setInitialDelay(0);

        this.currentFrameNumber = 1;
    }

    public void displayGUI() {
        // JFrame initialization
        Container contentPane = this.getContentPane();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        contentPane.setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;

        // TODO: Video Playback panel
        c.gridx = 0;
        c.gridy = 0;
        gridbag.setConstraints(videoPanel, c);
        contentPane.add(videoPanel);

        // control sub-panel
        controlPanel.initSubPanel(new PlaybackListener());
        c.gridx = 1;
        c.gridy = 0;
        gridbag.setConstraints(controlPanel, c);
        contentPane.add(controlPanel);

        // info sub-panel
        JLabel infoText = new JLabel("Blah");
        infoText.setForeground(Color.WHITE);
        infoPanel.add(infoText);
        infoPanel.setBackground(Color.BLACK);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 0;
        c.gridy = 1;
        gridbag.setConstraints(infoPanel, c);
        contentPane.add(infoPanel);

        // Last display steps
        this.pack();
        this.setVisible(true);
    }

    public void play() {
        ((JLabel) infoPanel.getComponent(0)).setText("Playing Frame " + this.currentFrameNumber);
        playbackTimer.start();
        audio.play();
    }

    public void pause() {
        ((JLabel) infoPanel.getComponent(0)).setText("Video Paused");
        audio.pause();
        playbackTimer.stop();
    }

    public void stop() {
        playbackTimer.stop();
        audio.stop();
        resetFrames();
    }

    private void resetFrames() {
        currentFrameNumber = 1;
        videoPanel.setIcon(new ImageIcon(video.getFrame(currentFrameNumber)));
        ((JLabel) infoPanel.getComponent(0)).setText("Video not started");
        revalidate();
        repaint();
    }
}
