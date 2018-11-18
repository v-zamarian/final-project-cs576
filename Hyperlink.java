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
    Point corner; //starting x,y location of top left corner of hyperlink box
    int boxWidth;
    int boxHeight;

    //these values are for the linear interpolation of the box while using the video player
    //the rates are how much to change each value per frame
    float cornerXRate; //how much to change the corner's x coordinate by
    float cornerYRate; //rate for y coordinate
    float widthRate; //rate for box width
    float heightRate; //rate for box height

    Color boxColor;

    public Hyperlink(String p, String s, int start, Color c){
        primaryName = p;
        startFrame = start;
        endFrame = -1;
        secondaryName = s;
        secondFrame = -1;
        corner = new Point(0,0);
        boxWidth = 0;
        boxHeight = 0;
        cornerXRate = 0.0f;
        cornerYRate = 0.0f;
        widthRate = 0.0f;
        heightRate = 0.0f;
        boxColor = c;
    }

    public Hyperlink(Hyperlink h){
        primaryName = h.primaryName;
        startFrame = h.startFrame;
        endFrame = h.endFrame;
        secondaryName = h.secondaryName;
        secondFrame = h.secondFrame;
        corner = h.corner;
        boxWidth = h.boxWidth;
        boxHeight = h.boxHeight;
        cornerXRate = h.cornerXRate;
        cornerYRate = h.cornerYRate;
        widthRate = h.widthRate;
        heightRate = h.heightRate;
        boxColor = h.boxColor;
    }

    //these values will define the hyperlink box to be displayed when playing the hyperlinked video
    public void setBoxParams(Point p, int w, int h){
        corner = p;
        boxWidth = w;
        boxHeight = h;
    }

    public void setRates(Point endPoint, int endWidth, int endHeight, int currentFrame){
        //the starting values will always be set by the time this method is called
        float totalFrames = currentFrame - startFrame; //how many frames the box will remain active for

        cornerXRate = (((endPoint.x - corner.x) / totalFrames) * 1000) / 1000;
        cornerYRate = (((endPoint.y - corner.y) / totalFrames) * 1000) / 1000;
        widthRate = (((endWidth - boxWidth) / totalFrames) * 1000) / 1000;
        heightRate = (((endHeight - boxHeight) / totalFrames) * 1000) / 1000;
    }

    public boolean setFrames(int endP, int startS){
        if (endP < startFrame){
            return false;
        }

        endFrame = endP;
        secondFrame = startS;

        return true;
    }

    @Override
    public String toString(){ //testing
        return String.format("{\'%s\', %d, %d, \'%s\', %d, (%d, %d), %d, %d, rates{(%.3f, %.3f), %.3f, %.3f}, (%d, %d, %d)}",
                primaryName, startFrame, endFrame, secondaryName, secondFrame, corner.x, corner.y,
                boxWidth, boxHeight, cornerXRate, cornerYRate, widthRate, heightRate,
                boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue());
    }
}
