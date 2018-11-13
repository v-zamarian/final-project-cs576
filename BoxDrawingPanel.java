//Victor Zamarian
//CS 576

//This class is used for drawing hyperlink boxes on top of the primary video frames


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class BoxDrawingPanel extends JPanel {
    int width = 352;
    int height = 288;
    int cornerSize = 8;

    //opposite corners of box
    Rectangle2D[] points = {new Rectangle2D.Double(150, 125, cornerSize, cornerSize),
            new Rectangle2D.Double(200, 165, cornerSize, cornerSize) };
    Rectangle2D s = new Rectangle2D.Double(); //box itself

    Color boxColor = Color.BLACK; //color of the box

    BoxMouseAdapter mAdapter = new BoxMouseAdapter();

    //these values will define the hyperlink box to be displayed when playing the hyperlinked video
    //keeping them here for now and will save them to the metadata file later
    int boxXPos, boxYPos, boxWidth, boxHeight;

    BoxDrawingPanel(){
        addMouseListener(mAdapter);
        addMouseMotionListener(mAdapter);
    }

    void selectColor(){
        boxColor = JColorChooser.showDialog(null, "Choose the box color", Color.RED);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(boxColor);

        //dashed line
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0, new float[]{7}, 0));

        for (int i = 0; i < points.length; i++){
            g2.fill(points[i]);
        }

        double newX = points[0].getCenterX();
        double newY = points[0].getCenterY();

        newX = Math.min(points[1].getCenterX(), newX); //not putting points[0] here since repaint lag occurs
        newY = Math.min(points[1].getCenterY(), newY);

        s.setRect(newX, newY, Math.abs(points[1].getCenterX() - points[0].getCenterX()),
                Math.abs(points[1].getCenterY() - points[0].getCenterY()));

        g2.draw(s);
    }

    class BoxMouseAdapter extends MouseAdapter {
        private int pos = -1;

        private void setBoxParams(){
            //x,y position of box is always its top left corner
            boxXPos = Math.min((int) points[0].getCenterX(), (int) points[1].getCenterX());
            boxYPos = Math.min((int) points[0].getCenterY(), (int) points[1].getCenterY());

            boxWidth = (int) Math.abs(points[1].getCenterX() - points[0].getCenterX());
            boxHeight = (int) Math.abs(points[1].getCenterY() - points[0].getCenterY());
        }

        @Override
        public void mouseMoved(MouseEvent e){
            //indicate that the box corners can be dragged by changing the mouse cursor
            if (points[0].contains(e.getPoint()) || points[1].contains(e.getPoint())) {
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }else{
                e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mousePressed(MouseEvent e){
            Point p = e.getPoint();

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

            setBoxParams();
        }

        @Override
        public void mouseDragged(MouseEvent e){
            if (pos == -1){
                return;
            }

            e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            //keep corners inside the frame boundaries
            int newX = Math.min(width-cornerSize/2, Math.max(-cornerSize/2, e.getX()));
            int newY = Math.min(height-cornerSize/2, Math.max(-cornerSize/2, e.getY()));

            points[pos].setRect(newX, newY, points[pos].getWidth(), points[pos].getHeight());

            repaint();
        }
    }
}

