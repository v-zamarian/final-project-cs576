//Victor Zamarian
//CS 576

//This file holds all the necessary information for a hyperlink.

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Hyperlink {
    //all of these values will be written to the metadata file
    String linkName; //name of the link
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

    public Hyperlink(String l, String p, String s, int start, Color c){
        linkName = l;
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
        linkName = h.linkName;
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

    //creates a hyperlink based on a string containing all values, like reverse toString
    public Hyperlink(String linkString){
        String strippedLinkString = linkString.replaceAll("\\s+", "");
        String[] properties = strippedLinkString.split(",");
        linkName = properties[0];
        primaryName = properties[1];
        startFrame = Integer.parseInt(properties[2]);
        endFrame = Integer.parseInt(properties[3]);
        secondaryName = properties[4];
        secondFrame = Integer.parseInt(properties[5]);
        corner = new Point(Integer.parseInt(properties[6]), Integer.parseInt(properties[7]));
        boxWidth = Integer.parseInt(properties[8]);
        boxHeight = Integer.parseInt(properties[9]);
        cornerXRate = Float.parseFloat(properties[10]);
        cornerYRate = Float.parseFloat(properties[11]);
        widthRate = Float.parseFloat(properties[12]);
        heightRate = Float.parseFloat(properties[13]);
        boxColor = new Color(Integer.parseInt(properties[14]),
                             Integer.parseInt(properties[15]),
                             Integer.parseInt(properties[16]));
//        Pattern pattern = Pattern.compile("(\\w+), (\\\\[a-zA-z0-9_ -]+)+, (\\d+), (\\d+), (\\\\[a-zA-z0-9_ -]+)+, " +
//                "(\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (-?\\d.\\d+), (-?\\d.\\d+), (-?\\d.\\d+), (-?\\d.\\d+), " +
//                "(\\d+), (\\d+), (\\d+)");
//        Matcher matcher = pattern.matcher(linkString);
//
//        if (matcher.matches()){
//            linkName = matcher.group(1);
//            primaryName = matcher.group(2);
//            startFrame = Integer.parseInt(matcher.group(3));
//            endFrame = Integer.parseInt(matcher.group(4));
//            secondaryName = matcher.group(5);
//            secondFrame = Integer.parseInt(matcher.group(6));
//            corner = new Point(Integer.parseInt(matcher.group(7)), Integer.parseInt(matcher.group(8)));
//            boxWidth = Integer.parseInt(matcher.group(9));
//            boxHeight = Integer.parseInt(matcher.group(10));
//            cornerXRate = Float.parseFloat(matcher.group(11));
//            cornerYRate = Float.parseFloat(matcher.group(12));
//            widthRate = Float.parseFloat(matcher.group(13));
//            heightRate = Float.parseFloat(matcher.group(14));
//            boxColor = new Color(Integer.parseInt(matcher.group(15)), Integer.parseInt(matcher.group(16)),
//                    Integer.parseInt(matcher.group(17)));
//        }
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

    public int getPrimaryStartFrame() {
        return this.startFrame;
    }

    public int getSecondaryStartFrame() {
        return this.secondFrame;
    }

    @Override
    public String toString(){ //this string is written to the metadata file
        return String.format("%s, %s, %d, %d, %s, %d, %d, %d, %d, %d, %.3f, %.3f, %.3f, %.3f, %d, %d, %d",
                linkName, primaryName, startFrame, endFrame, secondaryName, secondFrame,
                corner.x, corner.y, boxWidth, boxHeight, cornerXRate, cornerYRate, widthRate, heightRate,
                boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue());
    }
}