import java.awt.image.BufferedImage;

public class Video {
    private BufferedImage[] frames;
    private String filepath;
    private String videoName;
    private String videoDirectory;

    final private int NUM_FRAMES = 9000;

    public Video(String filepath) {
        this.frames = new BufferedImage[NUM_FRAMES];
        this.filepath = filepath;

        int p;
        if (System.getProperty("os.name").startsWith("Windows")) {
            p = filepath.lastIndexOf("\\");
        } else {
            p = filepath.lastIndexOf("/");
        }
        this.videoName = filepath.substring(p);
        this.videoDirectory = filepath.substring(0, p);
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

    public String getVideoDirectory() {
        return this.videoDirectory;
    }

    public BufferedImage[] getFrames() {
        return this.frames;
    }
}
