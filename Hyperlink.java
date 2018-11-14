//Victor Zamarian
//CS 576

//This file holds all the necessary information for a hyperlink.

import java.awt.*;

public class Hyperlink {
    //all of these values will be written to the metadata file
    String primaryName; //name of the primary video
    int startFrame; //of primary video
    int endFrame; //of primary video
    String secondaryName; //name of secondary video
    int secondFrame; //start frame of secondary video, -1 if link has not been created yet through control E
    Point corner; //x,y location of top left corner of hyperlink box
    int boxWidth;
    int boxHeight;
    Color boxColor;

    public Hyperlink(Color c){
        primaryName = "";
        startFrame = -1;
        endFrame = -1;
        secondaryName = "";
        secondFrame = -1;
        corner = new Point(0,0);
        boxWidth = 0;
        boxHeight = 0;
        boxColor = c;
    }

    //these values will define the hyperlink box to be displayed when playing the hyperlinked video
    public void setBoxParams(Point p, int w, int h){
        corner = p;
        boxWidth = w;
        boxHeight = h;
    }

    @Override
    public String toString(){ //testing
        return String.format("{\'%s\', %d, %d, \'%s\', %d, (%d, %d), %d, %d, (%d, %d, %d)}",
                primaryName, startFrame, endFrame, secondaryName, secondFrame, corner.x, corner.y,
                boxWidth, boxHeight, boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue());
    }
}
