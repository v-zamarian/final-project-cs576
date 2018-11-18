import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class HyperlinkVideoPanel extends JPanel {
    boolean linksLoaded = false;
    boolean videoPlaying = false;

    int alpha = 80; //how transparent hyperlink box should be

    int currentFrame;

    HashMap<String, Hyperlink> hyperlinks = new HashMap<>();
    HashMap<String, Hyperlink> currentBoxes = new HashMap<>();

    Rectangle2D s = new Rectangle2D.Double();

    BoxVideoMouseAdapter mAdapter = new BoxVideoMouseAdapter();

    HyperlinkVideoPanel(){
        addMouseListener(mAdapter);
    }

    void loadLinks(HashMap links){
        hyperlinks = (HashMap<String, Hyperlink>) links.clone();

        linksLoaded = true;

        repaint();
    }

    void playVideo(){ //quick testing code
        videoPlaying = true;

        Thread player = new Thread(new Runnable() {
            @Override
            public void run() {
                int fps = 30;
                currentFrame = 0;

                double updateTime = 1000 / (double) fps;
                long startTime = System.currentTimeMillis();
                long endTime = 0;

                while (true){
                    if ((endTime - startTime) >= updateTime){
                        currentFrame++;
                        repaint();

                        startTime = endTime;
                    }

                    endTime = System.currentTimeMillis();
                }
            }
        });

        player.start();
    }


    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        if (!linksLoaded){
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        for (Map.Entry<String, Hyperlink> entry : hyperlinks.entrySet()){
            String key = entry.getKey();
            Hyperlink currLink = entry.getValue();

            //only draw box in frames it was defined for
            if (currLink.endFrame != -1) {
                if (currentFrame < currLink.startFrame){
                    continue;
                }

                if (currentFrame > currLink.endFrame){
                    currentBoxes.remove(key);
                    continue;
                }
            }

            g2.setColor(new Color(currLink.boxColor.getRed(), currLink.boxColor.getGreen(), currLink.boxColor.getBlue(), alpha));

            int frameDiff = currentFrame - currLink.startFrame;

            double cornerX = currLink.corner.x + (frameDiff * currLink.cornerXRate);
            double cornerY = currLink.corner.y + (frameDiff * currLink.cornerYRate);

            double width = currLink.boxWidth + (frameDiff * currLink.widthRate);
            double height = currLink.boxHeight + (frameDiff * currLink.heightRate);

            Hyperlink temp = new Hyperlink(currLink);
            temp.setBoxParams(new Point((int) cornerX, (int) cornerY), (int) width, (int) height);
            currentBoxes.put(key, temp);

            s.setRect(cornerX, cornerY, width, height);

            g2.fill(s);
        }
    }

    class BoxVideoMouseAdapter extends MouseAdapter {
        String getLinkAtLocation(Point p){
            for (Map.Entry<String, Hyperlink> entry : currentBoxes.entrySet()){
                Hyperlink link = entry.getValue();

                Rectangle2D box = new Rectangle2D.Double(link.corner.x, link.corner.y, link.boxWidth, link.boxHeight);

                if (box.getBounds2D().contains(p.getX(), p.getY())){
                    return entry.getKey();
                }
            }

            return "";
        }

        @Override
        public void mouseMoved(MouseEvent e){
            if (getLinkAtLocation(e.getPoint()).equals("")){
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }

            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!videoPlaying) {
                playVideo();
            }else{
                String linkName = getLinkAtLocation(e.getPoint());

                if (!linkName.equals("")){
                    System.out.println("Clicked hyperlink: " + linkName);
                }
            }
        }
    }
}
