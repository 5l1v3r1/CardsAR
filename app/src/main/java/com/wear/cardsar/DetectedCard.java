package com.wear.cardsar;

import org.opencv.core.MatOfPoint;

import java.util.List;

/*
 Class which would represent a card detected on screen
 Unused since we haven't been able to detect a card
 */
public class DetectedCard {
    private List<MatOfPoint> mContours;
    int bestSuitMatch;
    int bestRankMatch;

    public DetectedCard(){

    }

    public void setContours(List<MatOfPoint> contours){
        mContours = contours;
    }

    public List<MatOfPoint> getContours(){
        return mContours;
    }
}
