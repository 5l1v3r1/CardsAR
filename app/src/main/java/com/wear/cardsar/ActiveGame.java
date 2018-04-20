package com.wear.cardsar;

import org.opencv.core.Mat;

public class ActiveGame extends Thread {
    private boolean runAlgorithm;
    private boolean killed;
    private DetectionAlgorithm da;
    private CameraView mCameraView;

    ActiveGame(CameraView cameraView, PlayingCardMappings mappings){
        super();

        runAlgorithm = false;
        killed = false;

        da = new DetectionAlgorithm();
        mCameraView = cameraView;
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

    void resumeWork(){
        runAlgorithm = true;
    }

    void pauseWork(){
        runAlgorithm = false;
    }

    void kill(){
        killed = true;
    }
}
