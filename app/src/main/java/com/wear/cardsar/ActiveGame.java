package com.wear.cardsar;

import org.opencv.core.Mat;

/*
 ActiveGame runs separate from the UI thread, handling the
 computer vision card detection algorithms. ActiveGame pulls
 the last input frame and sets the most recent output frame
 asynchronously as they become available/are computed.
 */
public class ActiveGame extends Thread {
    private boolean runAlgorithm;
    private boolean killed;
    private DetectionAlgorithm da;
    private CameraView mCameraView;
    private PlayingCardMappings mMappings;

    ActiveGame(CameraView cameraView, PlayingCardMappings mappings){
        super();

        runAlgorithm = false;
        killed = false;

        da = new DetectionAlgorithm();
        mCameraView = cameraView;
        mMappings = mappings;
    }

    @Override
    public void run() {
        // loop indefinitely while CameraView is active
        while (!killed) {
            if (runAlgorithm) {
                Mat inputFrame = new Mat();
                Mat outputFrame = new Mat();

                // attempt to copy the last frame captured
                if (mCameraView.getLastInput(inputFrame)){
                    da.processInput(inputFrame, outputFrame);

                    // push output back to camera view to be displayed
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
