package com.wear.cardsar;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by larsoe4 on 3/21/2018.
 */

public class DetectionAlgorithm extends Thread {

    private AndroidCameraApi mCameraAPI;
    private boolean runAlgorithm;
    private boolean killed;

    DetectionAlgorithm(AndroidCameraApi cameraAPI){
        mCameraAPI = cameraAPI;
        killed = false;
    }

    @Override
    public void run() {
        while (!killed) {
            if (runAlgorithm) {
                Mat inputFrame = new Mat();
                Mat outputFrame = new Mat();
                if (mCameraAPI.getLastInput(inputFrame)){
                    detectEdges(inputFrame, outputFrame);

                    mCameraAPI.setOutputFrame(outputFrame);
                }

                inputFrame.release();
                outputFrame.release();
            }
        }
    }

    public void resumeAlgorithm(){
        runAlgorithm = true;
    }

    public void pauseAlgorithm(){
        runAlgorithm = false;
    }

    public void kill(){
        killed = true;
    }

    public static void detectEdges(Mat frame, Mat output){
        if (frame == null) return;

        Mat edges = new Mat(frame.size(), CvType.CV_8UC1);

        Imgproc.cvtColor(frame, edges, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(edges, edges, 80, 100);

        //rotateNinety(edges, true);

        edges.copyTo(output);

        edges.release();
    }

    public static void rotateNinety(Mat frame, boolean clockwise){
        Size originalSize = frame.size();

        if (originalSize.area() == 0) return;


        Mat transpose = frame.t();
        if (clockwise){
            Core.flip(transpose, frame, 1);
        }else{
            Core.flip(transpose, frame, 0);
        }

        //Imgproc.resize(frame, frame, originalSize);

        transpose.release();
    }
}
