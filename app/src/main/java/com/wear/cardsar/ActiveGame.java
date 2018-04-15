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
    private Game mGame;

    private CardMapping[] mappings;

    public ActiveGame(CameraView cameraApi, Game game){
        super();

        runAlgorithm = false;
        killed = false;

        da = new DetectionAlgorithm();
        mCameraAPI = cameraApi;

        mGame = game;

        mappings = new CardMapping[52]; // TO-DO: Get mappings from mGame

        System.out.println("ActiveGame received " + game.getGameName());
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
