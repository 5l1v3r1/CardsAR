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
    private CameraView mCameraAPI;
    private Game mGame;

    private CardMapping[] mMappings;

    public ActiveGame(CameraView cameraApi, Game game, AppRepository repository){
        super();

        runAlgorithm = false;
        killed = false;

        da = new DetectionAlgorithm();
        mCameraAPI = cameraApi;

        mGame = game;

        mMappings = new CardMapping[52];
        initializeCardMappings(game, repository);

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

    private List<CardMapping> loadMappings(Game game, AppRepository repository){
        LiveData<List<CardMapping>> liveMappingData = repository.getMappings(game.getGameName());
        if (liveMappingData.getValue() != null){
            return liveMappingData.getValue();
        }else{
            return new LinkedList<>();
        }
    }

    private void initializeCardMappings(Game game, AppRepository repository){
        List<CardMapping> mappings = loadMappings(game, repository);

        int currentPCard = 0;
        for (CardMapping mapping : mappings){
            mMappings[currentPCard] = mapping;
            currentPCard++;

            //TODO: multiple decks?
            //TODO: currentPCard >= 52
            for (int i = 0; i < mapping.getQuantity(); i++){
                mMappings[currentPCard] = mapping;
                currentPCard++;
            }
        }
    }
}
