import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class VideoPlaybackTool extends JFrame {
    private Video video;
    private AudioWrapper audio;
    private VideoPlaybackControlPanel controlPanel;
    private JLabel videoPanel;
    private JPanel infoPanel;
    private Thread playbackThread;
    private int currentFrameNumber;
    private String hyperlinkPath;
    private HyperlinkVideoPanel hyperlinkVideoPanel;
    private boolean pauseRequested;
    private boolean stopRequested;
    private boolean isPlaying;

    final private int FRAMES_PER_SECOND = 30;
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

    public VideoPlaybackTool(String filepath, String hyperlinkPath) {
        this.setTitle("Hyperlinked Video Player");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.video = new Video(filepath);

        // init AudioWrapper object
        int p = filepath.lastIndexOf(".");
        String basePathName = filepath.substring(0, p);
        this.audio = new AudioWrapper(basePathName);

        this.controlPanel = new VideoPlaybackControlPanel();
        this.videoPanel = new JLabel(new ImageIcon(this.video.getFrame(1)));
        this.infoPanel = new JPanel();
        this.hyperlinkVideoPanel = new HyperlinkVideoPanel();

        this.currentFrameNumber = 1;
        this.hyperlinkPath = hyperlinkPath;

        this.isPlaying = false;
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

        // Hyperlink Video Panel
        //hyperlinkVideoPanel.setOpaque(false);
        hyperlinkVideoPanel.setBackground(Color.BLUE);
        gridbag.setConstraints(hyperlinkVideoPanel, c);
        contentPane.add(hyperlinkVideoPanel);
        hyperlinkVideoPanel.loadLinks(hyperlinkPath);

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
        if (isPlaying) {
            return;
        }

        if (pauseRequested) {
            pauseRequested = false;
            synchronized(playbackThread) {
                playbackThread.notify();
            }
        }

        playbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                double updateTime = 1000 / (double) FRAMES_PER_SECOND;
                long startTime = System.currentTimeMillis();
                long endTime = 0;

                isPlaying = true;
                audio.play();
                while (true) {
                    if (stopRequested) {
                        break;
                    }

                    if (pauseRequested) {
                        try {
                            synchronized(this) {
                                while (pauseRequested) {
                                    this.wait();
                                }

                                isPlaying = true;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if ((endTime - startTime) >= updateTime){
                        ((JLabel) infoPanel.getComponent(0)).setText("Playing Frame " + currentFrameNumber);

                        if (currentFrameNumber == TOTAL_FRAMES) {
                            resetFrames();
                            revalidate();
                            repaint();

                            return;
                        }

                        if (currentFrameNumber == 1) {
                            System.out.println("test");
                        }

                        if (currentFrameNumber == TOTAL_FRAMES) {
                            audio.stop();
                            break;
                        }

                        videoPanel.setIcon(new ImageIcon(video.getFrame(currentFrameNumber)));
                        ((JLabel) infoPanel.getComponent(0)).setText("Playing frame " + currentFrameNumber);

                        hyperlinkVideoPanel.setCurrentFrame(currentFrameNumber);
                        hyperlinkVideoPanel.updateHyperlinks();
                        getContentPane().revalidate();
                        getContentPane().repaint();

                        currentFrameNumber++;

                        startTime = endTime;
                    }

                    endTime = System.currentTimeMillis();
                }
            }
        });

        playbackThread.start();
    }

    public void pause() {
        if (!isPlaying) {
            return;
        }

        isPlaying = false;
        pauseRequested = true;
        ((JLabel) infoPanel.getComponent(0)).setText("Video Paused at Frame " + currentFrameNumber);
        audio.pause();
    }

    public void stop() {
        isPlaying = false;
        stopRequested = true;
        pauseRequested = false;
        synchronized (playbackThread) {
            playbackThread.notify();
        }

        resetFrames();
        audio.stop();
    }

    private void resetFrames() {
        isPlaying = false;
        pauseRequested = false;
        stopRequested = false;

        currentFrameNumber = 1;
        videoPanel.setIcon(new ImageIcon(video.getFrame(currentFrameNumber)));
        ((JLabel) infoPanel.getComponent(0)).setText("Video not started");
        revalidate();
        repaint();
    }
}
