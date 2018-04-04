package com.wear.cardsar;

import org.opencv.core.MatOfPoint;

import java.util.List;

/**
 * Created by larsoe4 on 4/2/2018.
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
