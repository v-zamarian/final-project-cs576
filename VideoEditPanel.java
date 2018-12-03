import javafx.scene.control.Hyperlink;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class VideoEditPanel extends JPanel {
    private char id;
    private String video;
    private String videoPath;
    private BufferedImage frame;
    private int currentFrameNumber;
    private Map<String, BufferedImage> cache;
    private HyperlinkPanel hyperlinkPanel;

    final private static int IMG_WIDTH = 352;
    final private static int IMG_HEIGHT = 288;

    public VideoEditPanel(char id) {
        this.id = id;
        this.cache = new HashMap<>();
        this.frame = null;
    }

    public VideoEditPanel(char id, String videoPath) {
        this.id = id;
        this.cache = new HashMap<>();

        int p = videoPath.lastIndexOf(".");
        String videoPathName = videoPath.substring(0, p);
        this.video = videoPathName;
        this.videoPath = videoPath;

        // load first frame
        try {
            BufferedImage firstFrame = readFrame(videoPathName + "0001.rgb");
            this.cache.put(id + video + "1", firstFrame);
            this.frame = firstFrame;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char getID() {
        return id;
    }

    public void loadVideo(String videoPath) {
        // flush cache and reload instance vars for new video
        int p = videoPath.lastIndexOf(".");
        String videoPathName = videoPath.substring(0, p);
        this.video = videoPathName;
        this.videoPath = videoPath;
        this.cache = new HashMap<>();

        // load first frame
        try {
            BufferedImage firstFrame = readFrame(videoPathName + "0001.rgb");
            this.cache.put(id + video + "1", firstFrame);
            this.frame = firstFrame;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.currentFrameNumber = 1;
        ((JLabel) this.getComponent(0)).setIcon(new ImageIcon(this.getFrame()));

        this.getParent().revalidate();
        this.getParent().repaint();
    }

    public void setFrame(int frameNumber) {
        if (!cache.containsKey(frameNumber)) {
            String frameNumberZeroPad = String.format("%04d", frameNumber);

            BufferedImage frame = null;
            try {
                frame = readFrame(video + frameNumberZeroPad + ".rgb");
            } catch (IOException e) {
                e.printStackTrace();
            }

            cache.put(id + video + frameNumber, frame);
        }

        this.frame = cache.get(id + video + frameNumber);
        this.currentFrameNumber = frameNumber;
    }

    public boolean isVideoSet() {
        return (this.frame != null);
    }

    public String getVideoPath() {
        return this.videoPath;
    }

    public HyperlinkPanel getHyperlinkPanel() {
        return this.hyperlinkPanel;
    }

    public void setHyperlinkPanel(HyperlinkPanel hyperlinkPanel) {
        this.hyperlinkPanel = hyperlinkPanel;
    }

    public int getCurrentFrameNumber() {
        return this.currentFrameNumber;
    }

    public void setCurrentFrameNumber(int frameNumber) {
        this.currentFrameNumber = frameNumber;
    }

    public BufferedImage getFrame() {
        return frame;
    }

    public static BufferedImage readFrame(String imageName) throws IOException {
        byte[] rawBytes = Files.readAllBytes(new File(imageName).toPath());
        DataBuffer imgData = new DataBufferByte(rawBytes, rawBytes.length);
        SampleModel compModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, IMG_WIDTH, IMG_HEIGHT, 1, IMG_WIDTH, new int[]{0, IMG_WIDTH*IMG_HEIGHT, IMG_WIDTH*IMG_HEIGHT*2});
        Raster raster = Raster.createRaster(compModel, imgData, null);

        BufferedImage img = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        img.setData(raster);

        return img;
    }
}
