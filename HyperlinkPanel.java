
//Victor Zamarian
//CS 576

//This class is used for drawing hyperlink boxes on top of the primary video frames.

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HyperlinkPanel extends JPanel {
    int width = 352;
    int height = 288;
    int cornerSize = 8;

    private HashMap<String, Hyperlink> hyperlinks = new HashMap<>();
    private HashMap<String, Rectangle2D[]> hyperlinkBoxes = new HashMap<>();
    private Rectangle2D s = new Rectangle2D.Double(); //box itself
    private VideoEditPanel videoPanel;
    private VideoToolVideoPanel vtvp;

    BoxMouseAdapter mAdapter = new BoxMouseAdapter();

    HyperlinkPanel(){
        addMouseListener(mAdapter);
        addMouseMotionListener(mAdapter);
    }

    void addHyperlink(String linkName, String p, String s, int start, Color c){
        hyperlinks.put(linkName, new Hyperlink(linkName, p, s, start, c));

        //put initial box in default position
        hyperlinkBoxes.put(linkName, new Rectangle2D[] {new Rectangle2D.Double(150, 125, cornerSize, cornerSize),
                new Rectangle2D.Double(200, 165, cornerSize, cornerSize)} );

        setBoxParams(linkName);

        repaint();
    }

    void renameHyperlink(String oldName, String newName){
        hyperlinks.put(newName, new Hyperlink(hyperlinks.get(oldName)));
        hyperlinkBoxes.put(newName, hyperlinkBoxes.get(oldName));

        hyperlinks.remove(oldName);
        hyperlinkBoxes.remove(oldName);
    }

    void changeLinkColor(String linkName, Color c){
        hyperlinks.get(linkName).boxColor = c;

        repaint();
    }

    Hyperlink getHyperlink(String linkName){
        return hyperlinks.get(linkName);
    }

    HashMap<String, Hyperlink> getHyperlinkList(){
        return hyperlinks;
    }

    int setFrames(String linkName, int primaryEnd, int secondaryStart){
        if (linkName.equals("")){
            return 2;
        }

        repaint();

        return hyperlinks.get(linkName).setFrames(primaryEnd, secondaryStart) ? 1 : 0;
    }

    private void setBoxParams(String linkName){
        if (linkName.equals("") || isConnected(linkName)){
            return;
        }

        Rectangle2D[] points = hyperlinkBoxes.get(linkName);

        // get params from drag points and set the box params
        int boxXPos = Math.min((int) points[0].getCenterX(), (int) points[1].getCenterX());
        int boxYPos = Math.min((int) points[0].getCenterY(), (int) points[1].getCenterY());
        int boxWidth = (int) Math.abs(points[1].getCenterX() - points[0].getCenterX());
        int boxHeight = (int) Math.abs(points[1].getCenterY() - points[0].getCenterY());

        // TODO: this is temporary if time allows
        if (videoPanel == null) {
            videoPanel = vtvp.getVideoSubPanel('A');
        }

        if (videoPanel.getCurrentFrameNumber() == hyperlinks.get(linkName).startFrame){
            hyperlinks.get(linkName).setBoxParams(new Point(boxXPos, boxYPos), boxWidth, boxHeight);
        }else{
            hyperlinks.get(linkName).setRates(new Point(boxXPos, boxYPos), boxWidth, boxHeight, videoPanel.getCurrentFrameNumber());
        }
    }

    //checks if the current hyperlink being edited has been connected to a secondary video yet
    boolean isConnected(String name){
        if (!hyperlinks.containsKey(name)) {
            return false;
        }
        return hyperlinks.get(name).secondFrame != -1;
    }

    //removes a hyperlink
    void removeLink(String name){
        hyperlinks.remove(name);
        hyperlinkBoxes.remove(name);

        repaint();
    }

    String saveHyperlinks(String originalFile){
        //select directory to save hyperlink file to
        //output file name is the primary video's name.hyp
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        String outputFile;
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            // Get filename depending on OS file structure
            if (System.getProperty("os.name").startsWith("Windows")) {
                originalFile = originalFile.substring(originalFile.lastIndexOf("\\"));
            } else {
                originalFile = originalFile.substring(originalFile.lastIndexOf("/"));
            }


            outputFile = chooser.getSelectedFile().getAbsolutePath() + originalFile + ".hyp";

            System.out.println("Saving hyperlinks to the directory: " + outputFile); //temp
        }else{
            return null;
        }

        //save hyperlinks to the file
        try{
            FileWriter out = new FileWriter(outputFile);

            for (Map.Entry<String, Hyperlink> entry : hyperlinks.entrySet()) {
                out.write(entry.getValue().toString());
                out.write("\r\n");
            }

            out.close();
        } catch (IOException io){
            System.out.println("ERROR: " + io.getMessage());
        }

        return outputFile;
    }

    //sets the drawing rectangle to the params of the current hyperlink to draw
    void setHyperlinkBox(Map.Entry<String, Rectangle2D[]> entry){
        String key = entry.getKey();
        Rectangle2D[] points = entry.getValue();

        if (!isConnected(key)) {
            double newX = points[0].getCenterX();
            double newY = points[0].getCenterY();

            newX = Math.min(points[1].getCenterX(), newX); //not putting points[0] here since repaint lag occurs
            newY = Math.min(points[1].getCenterY(), newY);

            s.setRect(newX, newY, Math.abs(points[1].getCenterX() - points[0].getCenterX()),
                    Math.abs(points[1].getCenterY() - points[0].getCenterY()));
        }else{
            Hyperlink currLink = hyperlinks.get(key);

            int frameDiff = videoPanel.getCurrentFrameNumber() - currLink.startFrame;

            double cornerX = currLink.corner.x + (frameDiff * currLink.cornerXRate);
            double cornerY = currLink.corner.y + (frameDiff * currLink.cornerYRate);

            double width = currLink.boxWidth + (frameDiff * currLink.widthRate);
            double height = currLink.boxHeight + (frameDiff * currLink.heightRate);

            s.setRect(cornerX, cornerY, width, height);
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        // lazy load parent because it returns null during construction by design
        if (vtvp == null) {
            vtvp = (VideoToolVideoPanel) SwingUtilities.getAncestorOfClass(VideoToolVideoPanel.class, this);
            videoPanel = vtvp.getVideoSubPanel('A');
        }

        Graphics2D g2 = (Graphics2D) g;
        for (Map.Entry<String, Rectangle2D[]> entry : hyperlinkBoxes.entrySet()){
            String key = entry.getKey();
            Rectangle2D[] points = entry.getValue();

            //after connecting the hyperlink, only draw the box on the frames it was linked to
            if (isConnected(key)) {
                if (videoPanel.getCurrentFrameNumber() < hyperlinks.get(key).startFrame ||
                        videoPanel.getCurrentFrameNumber() > hyperlinks.get(key).endFrame) {
                    continue;
                }
            }

            g2.setColor(hyperlinks.get(key).boxColor);

            if (vtvp != null && key.equals(vtvp.getCurrentLink())){ //dashed line for current box to be edited
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{7}, 0));

                if (!isConnected(key)) {
                    g2.fill(points[0]);
                    g2.fill(points[1]);
                }
            }else{
                g2.setStroke(new BasicStroke(2));
            }

            setHyperlinkBox(entry);

            g2.draw(s);
        }

        // for initial construction
        if (vtvp != null) {
            setBoxParams(vtvp.getCurrentLink());
        }
    }

    class BoxMouseAdapter extends MouseAdapter {
        private int pos = -1;

        public BoxMouseAdapter() {
            // lazy load in case of BoxMouseAdapter wins race against paintComponent
            if (vtvp == null) {
                vtvp = (VideoToolVideoPanel) SwingUtilities.getAncestorOfClass(VideoToolVideoPanel.class, HyperlinkPanel.this);
                //videoPanel = vtvp.getVideoSubPanel('A');
            }
        }

        @Override
        public void mouseMoved(MouseEvent e){
            if (vtvp == null) {
                vtvp = (VideoToolVideoPanel) SwingUtilities.getAncestorOfClass(VideoToolVideoPanel.class, HyperlinkPanel.this);
                //videoPanel = vtvp.getVideoSubPanel('A');
            }

            if (vtvp.getCurrentLink().equals("") || isConnected(vtvp.getCurrentLink())){
                return;
            }

            //indicate that the box corners can be dragged by changing the mouse cursor
            Rectangle2D[] points = hyperlinkBoxes.get(vtvp.getCurrentLink());

            if (points[0].contains(e.getPoint()) || points[1].contains(e.getPoint())) {
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }else{
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mousePressed(MouseEvent e){
            if (vtvp.getCurrentLink().equals("") || isConnected(vtvp.getCurrentLink())){
                return;
            }

            Point p = e.getPoint();

            Rectangle2D[] points = hyperlinkBoxes.get(vtvp.getCurrentLink());

            for (int i = 0; i < points.length; i++){
                if (points[i].contains(p)){
                    pos = i;
                    return;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e){
            pos = -1;
            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        public void mouseDragged(MouseEvent e){
            if (pos == -1){
                return;
            }

            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            Rectangle2D[] points = hyperlinkBoxes.get(vtvp.getCurrentLink());

            //keep corners inside the frame boundaries
            int newX = Math.min(width-cornerSize/2, Math.max(-cornerSize/2, e.getX()));
            int newY = Math.min(height-cornerSize/2, Math.max(-cornerSize/2, e.getY()));

            points[pos].setRect(newX, newY, points[pos].getWidth(), points[pos].getHeight());

            repaint();
        }
    }
}