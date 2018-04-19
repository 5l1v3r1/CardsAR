package com.wear.cardsar;

import android.arch.lifecycle.LiveData;

import org.opencv.core.Mat;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by larsoe4 on 4/2/2018.
 */

public class ActiveGame extends Thread {
    private boolean runAlgorithm;
    private boolean killed;
    private DetectionAlgorithm da;
    private CameraView mCameraView;

    private PlayingCardMappings mMappings;

    public ActiveGame(CameraView cameraView, PlayingCardMappings mappings){
        super();

        runAlgorithm = false;
        killed = false;

        da = new DetectionAlgorithm();
        mCameraView = cameraView;

        mMappings = mappings;
    }

    @Override
    public void run() {
        while (!killed) {

            if (runAlgorithm) {
                Mat inputFrame = new Mat();
                Mat outputFrame = new Mat();
                if (mCameraView.getLastInput(inputFrame)){

                    da.processInput(inputFrame, outputFrame);

                    mCameraView.setOutputFrame(outputFrame);
                    inputFrame.release();
                    outputFrame.release();
                }


            }

        }
    }

    public void resumeWork(){
        runAlgorithm = true;
    }

    public void pauseWork(){
        runAlgorithm = false;
    }

    public void kill(){
        killed = true;
    }
}
