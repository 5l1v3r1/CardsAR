package com.wear.cardsar;

import org.opencv.core.Mat;

/**
 * Created by larsoe4 on 4/2/2018.
 */

public class ActiveGame extends Thread {
    private boolean runAlgorithm;
    private boolean killed;
    private DetectionAlgorithm da;
    private CameraView mCameraAPI;

    private CardMapping[] mappings;

    public ActiveGame(CameraView cameraApi){
        super();

        runAlgorithm = false;
        killed = false;

        da = new DetectionAlgorithm();
        mCameraAPI = cameraApi;

        mappings = new CardMapping[52];
    }

    @Override
    public void run() {
        while (!killed) {
            if (runAlgorithm) {
                Mat inputFrame = new Mat();
                Mat outputFrame = new Mat();
                if (mCameraAPI.getLastInput(inputFrame)){

                    da.processInput(inputFrame, outputFrame);

                    mCameraAPI.setOutputFrame(outputFrame);
                }

                inputFrame.release();
                outputFrame.release();
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
