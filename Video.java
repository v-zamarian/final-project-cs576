import java.awt.image.BufferedImage;

public class Video {
    private BufferedImage[] frames;
    private String filepath;
    private String videoPathName;
    private String videoName;
    private String videoDirectory;

    final private int NUM_FRAMES = 9000;

    public Video(String filepath) {
        this.frames = new BufferedImage[NUM_FRAMES];

        // get filepath without extension
        int p = filepath.lastIndexOf(".");
        this.videoPathName = filepath.substring(0, p);
        this.filepath = videoPathName;

        if (System.getProperty("os.name").startsWith("Windows")) {
            p = filepath.lastIndexOf("\\");
        } else {
            p = filepath.lastIndexOf("/");
        }
        this.videoName = filepath.substring(p);
        this.videoDirectory = filepath.substring(0, p);
        System.out.println("VIDEO NAME: " + this.videoName);

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

    public String getVideoDirectory() {
        return this.videoDirectory;
    }

    public BufferedImage[] getFrames() {
        return this.frames;
    }
}
