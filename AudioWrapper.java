import javax.sound.sampled.*;
import java.io.*;

public class AudioWrapper {
    private String filepath;
    private Clip audio;
    private AudioInputStream audioStream;

    public AudioWrapper(String baseNamePath) {
        this.filepath = baseNamePath + ".wav";

        try {
            this.audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(filepath)));
            DataLine.Info audioData = new DataLine.Info(Clip.class, this.audioStream.getFormat());
            this.audio = (Clip) AudioSystem.getLine(audioData);
            this.audio.open(this.audioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        audio.start();
    }

    public void stop() {
        audio.stop();
        audio.setFramePosition(0);
    }

    public void pause() {
        audio.stop();
    }

    public Clip getAudio() {
        return this.audio;
    }

    protected void finalize() throws Throwable {
        super.finalize();

        audio.close();
    }
}
