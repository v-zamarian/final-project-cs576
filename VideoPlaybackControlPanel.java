import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VideoPlaybackControlPanel extends JPanel {
    private JButton play;
    private JButton pause;
    private JButton stop;

    public VideoPlaybackControlPanel() {}

    public void initSubPanel(ActionListener playback) {
        this.setLayout(new GridLayout(3, 1));
        this.setBackground(Color.BLACK);
        initControlButtons(playback);
    }

    private void initControlButtons(ActionListener playback) {
        play = new JButton(new ImageIcon("play_button.png"));
        play.setBorderPainted(false);
        play.setContentAreaFilled(false);
        play.setFocusPainted(false);
        play.setOpaque(false);
        play.addActionListener(playback);
        play.setCursor((Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)));

        pause = new JButton(new ImageIcon("pause_button.png"));
        pause.setBorderPainted(false);
        pause.setContentAreaFilled(false);
        pause.setFocusPainted(false);
        pause.setOpaque(false);
        pause.addActionListener(playback);
        pause.setCursor((Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)));

        stop = new JButton(new ImageIcon("stop_button.png"));
        stop.setBorderPainted(false);
        stop.setContentAreaFilled(false);
        stop.setFocusPainted(false);
        stop.setOpaque(false);
        stop.addActionListener(playback);
        stop.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.add(play);
        this.add(pause);
        this.add(stop);
    }

    public JButton getPlay() {
        return this.play;
    }

    public JButton getPause() {
        return this.pause;
    }

    public JButton getStop() {
        return this.stop;
    }
}
