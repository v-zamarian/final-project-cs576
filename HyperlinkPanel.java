//Victor Zamarian
//CS 576

//This class is used for drawing hyperlink boxes on top of the primary video frames

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class HyperlinkPanel extends JPanel {
    int width = 352;
    int height = 288;
    int cornerSize = 8;

    private HashMap<String, Hyperlink> hyperlinks = new HashMap<>();
    private HashMap<String, Rectangle2D[]> hyperlinkBoxes = new HashMap<>();
    Rectangle2D s = new Rectangle2D.Double(); //box itself

    BoxMouseAdapter mAdapter = new BoxMouseAdapter();

    HyperlinkPanel(){
        addMouseListener(mAdapter);
        addMouseMotionListener(mAdapter);
    }

    void addHyperlink(String linkName, String p, String s, int start, Color c){
        hyperlinks.put(linkName, new Hyperlink(p, s, start, c));

        //put initial box in default position
        hyperlinkBoxes.put(linkName, new Rectangle2D[] {new Rectangle2D.Double(150, 125, cornerSize, cornerSize),
                new Rectangle2D.Double(200, 165, cornerSize, cornerSize)} );

        setBoxParams();

        repaint();
    }

    Hyperlink getHyperlink(String linkName){
        return hyperlinks.get(linkName);
    }

    HashMap getHyperlinkList(){
        return hyperlinks;
    }

    int setFrames(int primaryEnd, int secondaryStart){
        if (VideoTool.currentLink.equals("")){
            return 2;
        }

        repaint();

        return hyperlinks.get(VideoTool.currentLink).setFrames(primaryEnd, secondaryStart) ? 1 : 0;
    }

    private void setBoxParams(){
        if (VideoTool.currentLink.equals("") || isConnected(VideoTool.currentLink)){
            return;
        }

        Rectangle2D[] points = hyperlinkBoxes.get(VideoTool.currentLink);

        //x,y position of box is always its top left corner
        int boxXPos = Math.min((int) points[0].getCenterX(), (int) points[1].getCenterX());
        int boxYPos = Math.min((int) points[0].getCenterY(), (int) points[1].getCenterY());

        int boxWidth = (int) Math.abs(points[1].getCenterX() - points[0].getCenterX());
        int boxHeight = (int) Math.abs(points[1].getCenterY() - points[0].getCenterY());

        if (VideoTool.currentFrameP == hyperlinks.get(VideoTool.currentLink).startFrame){
            hyperlinks.get(VideoTool.currentLink).setBoxParams(new Point(boxXPos, boxYPos), boxWidth, boxHeight);
        }else{
            hyperlinks.get(VideoTool.currentLink).setRates(new Point(boxXPos, boxYPos), boxWidth, boxHeight, VideoTool.currentFrameP);
        }
    }

    //checks if the current hyperlink being edited has been connected to a secondary video yet
    boolean isConnected(String name){
        return hyperlinks.get(name).secondFrame != -1;
    }

    //removes a hyperlink
    void removeLink(String name){
        hyperlinks.remove(name);
        hyperlinkBoxes.remove(name);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (Map.Entry<String, Rectangle2D[]> entry : hyperlinkBoxes.entrySet()){
            String key = entry.getKey();
            Rectangle2D[] points = entry.getValue();

            //after connecting the hyperlink, only draw the box on the frames it was linked to
            if (hyperlinks.get(key).endFrame != -1) {
                if (VideoTool.currentFrameP < hyperlinks.get(key).startFrame ||
                        VideoTool.currentFrameP > hyperlinks.get(key).endFrame) {
                    continue;
                }
            }

            g2.setColor(hyperlinks.get(key).boxColor);

            if (key.equals(VideoTool.currentLink)){ //dashed line for current box to be edited
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{7}, 0));

                if (!isConnected(key)) {
                    g2.fill(points[0]);
                    g2.fill(points[1]);
                }
            }else{
                g2.setStroke(new BasicStroke(2));
            }

            double newX = points[0].getCenterX();
            double newY = points[0].getCenterY();

            newX = Math.min(points[1].getCenterX(), newX); //not putting points[0] here since repaint lag occurs
            newY = Math.min(points[1].getCenterY(), newY);

            s.setRect(newX, newY, Math.abs(points[1].getCenterX() - points[0].getCenterX()),
                    Math.abs(points[1].getCenterY() - points[0].getCenterY() ));

            g2.draw(s);
        }

        setBoxParams();
    }

    class BoxMouseAdapter extends MouseAdapter {
        private int pos = -1;

        @Override
        public void mouseMoved(MouseEvent e){
            if (VideoTool.currentLink.equals("") || isConnected(VideoTool.currentLink)){
                return;
            }

            //indicate that the box corners can be dragged by changing the mouse cursor
            Rectangle2D[] points = hyperlinkBoxes.get(VideoTool.currentLink);

            if (points[0].contains(e.getPoint()) || points[1].contains(e.getPoint())) {
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }else{
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mousePressed(MouseEvent e){
            if (VideoTool.currentLink.equals("") || isConnected(VideoTool.currentLink)){
                return;
            }

            Point p = e.getPoint();

            Rectangle2D[] points = hyperlinkBoxes.get(VideoTool.currentLink);

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

            Rectangle2D[] points = hyperlinkBoxes.get(VideoTool.currentLink);

            //keep corners inside the frame boundaries
            int newX = Math.min(width-cornerSize/2, Math.max(-cornerSize/2, e.getX()));
            int newY = Math.min(height-cornerSize/2, Math.max(-cornerSize/2, e.getY()));

            points[pos].setRect(newX, newY, points[pos].getWidth(), points[pos].getHeight());

            repaint();
        }
    }
}

