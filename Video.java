import java.awt.image.BufferedImage;
import java.io.IOException;

public class Video {
    private BufferedImage[] frames;
    private String filepath;
    private String videoPathName;
    private String videoName;

    final private int NUM_FRAMES = 9000;

    public Video(String filepath) {
        this.frames = new BufferedImage[NUM_FRAMES];

        // get filepath without extension
        int p = filepath.lastIndexOf(".");
        this.videoPathName = filepath.substring(0, p);
        this.videoName = filepath.substring(p);
        this.filepath = videoPathName;

        // load frames into buffer
////        for (int ii = 0; ii < NUM_FRAMES; ii++) {
////            String frameNumber = String.format("%04d", ii+1);
////            try {
////                frames[ii] = VideoEditPanel.readFrame(this.videoPathName + frameNumber + ".rgb");
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
    }

    public String getFilepath() {
        return this.filepath;
    }

    public String getVideoName() {
        return this.videoName;
    }

    public BufferedImage getFrame(int frameNumber) {
        BufferedImage img = null;
        try {
            img = VideoEditPanel.readFrame(getFramePath(frameNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return img;
    }

    public String getFramePath(int frameNumber) {
        return String.format(this.filepath + "%04d.rgb", frameNumber);
    }

    public BufferedImage[] getFrames() {
        return this.frames;
    }
}
