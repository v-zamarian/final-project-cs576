import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class VideoPlaybackTool extends JFrame {
    private Video video;
    private AudioWrapper audio;
    private VideoPlaybackControlPanel controlPanel;
    private JLabel videoImageLabel;
    private JPanel infoPanel;
    private GridBagConstraints c;
    private Thread playbackThread;
    private int currentFrameNumber;
    private HyperlinkVideoPanel hyperlinkVideoPanel;
    private boolean pauseRequested;
    private boolean stopRequested;
    private boolean isPlaying;

    private boolean fromLink;

    final private int FRAMES_PER_SECOND = 30;
    final private int TOTAL_FRAMES = 9000;

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

    private class WindowCloseListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            audio.stop();
            e.getWindow().dispose();
        }
    }

    public VideoPlaybackTool(String filepath, int frameNumber, boolean fromLink) {
        this.setTitle("Hyperlinked Video Player");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowCloseListener());
        this.video = new Video(filepath);

        // init AudioWrapper object
        int p = filepath.lastIndexOf(".");
        String basePathName = filepath.substring(0, p);
        this.audio = new AudioWrapper(basePathName);
        if (fromLink) {
            int audioFrame = (frameNumber*audio.getAudio().getFrameLength())/TOTAL_FRAMES;
            audio.getAudio().setFramePosition(audioFrame);
        }

        this.c = new GridBagConstraints();
        this.controlPanel = new VideoPlaybackControlPanel();
        this.videoImageLabel = new JLabel(new ImageIcon(this.video.getFrame(frameNumber)));
        this.infoPanel = new JPanel();
        this.hyperlinkVideoPanel = new HyperlinkVideoPanel();

        this.currentFrameNumber = frameNumber;

        this.isPlaying = false;

        this.fromLink = fromLink;
    }

    public void displayGUI() throws Exception {
        // JFrame initialization
        Container contentPane = this.getContentPane();
        GridBagLayout gridbag = new GridBagLayout();
        contentPane.setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;

        // Video Playback Panel with hyperlinkVideoPanel and videoImageLabel
        initVideoPanel(0, 0);

        // control sub-panel
        controlPanel.initSubPanel(new PlaybackListener());
        c.gridx = 1;
        c.gridy = 0;
        gridbag.setConstraints(controlPanel, c);
        contentPane.add(controlPanel);

        // info sub-panel
        JLabel infoText = new JLabel("Video not started");
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

                        videoImageLabel.setIcon(new ImageIcon(video.getFrame(currentFrameNumber)));
                        ((JLabel) infoPanel.getComponent(0)).setText("Playing frame " + currentFrameNumber);

                        hyperlinkVideoPanel.setCurrentFrame(currentFrameNumber);
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
        videoImageLabel.setIcon(new ImageIcon(video.getFrame(currentFrameNumber)));
        ((JLabel) infoPanel.getComponent(0)).setText("Video not started");
        revalidate();
        repaint();
    }

    private void initVideoPanel(int x, int y) throws Exception {
        JPanel videoPlaybackPanel = new JPanel();
        videoPlaybackPanel.setBorder(new EmptyBorder(-5, -5, -5, -5));
        c.gridx = x;
        c.gridy = y;

        hyperlinkVideoPanel.setOpaque(false);
        hyperlinkVideoPanel.setPreferredSize(new Dimension(352, 288));

        // load links depending on if caller is edit panel or another video playback tool
        boolean linksLoaded;
        if (fromLink) {
            String linkPath = video.getVideoDirectory() + video.getVideoName();

            linksLoaded = hyperlinkVideoPanel.loadLinks(linkPath);
        } else {
            linksLoaded = hyperlinkVideoPanel.loadLinks(null);
        }
        if (!fromLink && !linksLoaded) {
            throw new Exception("User cancelled operation");
        }

        videoPlaybackPanel.add(videoImageLabel);

        this.getContentPane().add(hyperlinkVideoPanel, c);
        this.getContentPane().add(videoPlaybackPanel, c);
    }

    public void triggerLinkAction(Hyperlink link) {
        pause();

        try {
            (new VideoPlaybackTool(link.getSecondaryName(), link.getSecondaryStartFrame(), true)).displayGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
