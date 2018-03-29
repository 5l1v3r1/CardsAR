package com.wear.cardsar;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

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

    public void processInput(Mat input, Mat output){

        detectEdges(input, output);

        pointsOfInterest(output, input);

        input.copyTo(output);
    }

    public void detectEdges(Mat frame, Mat output){
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

    public void pointsOfInterest(Mat edgesFrame, Mat original){
        Mat mRgba = original;
        Mat mGray = edgesFrame;


        Scalar CONTOUR_COLOR = new Scalar(255);
        MatOfKeyPoint keypoint = new MatOfKeyPoint();
        List<KeyPoint> listpoint;
        KeyPoint kpoint;
        Mat mask = Mat.zeros(mGray.size(), CvType.CV_8UC1);
        int rectanx1;
        int rectany1;
        int rectanx2;
        int rectany2;

        //
        Scalar zeos = new Scalar(0, 0, 0);
        List<MatOfPoint> contour2 = new ArrayList<MatOfPoint>();
        Mat kernel = new Mat(1, 50, CvType.CV_8UC1, Scalar.all(255));
        Mat morbyte = new Mat();
        Mat hierarchy = new Mat();

        Rect rectan3;
        int imgsize = mRgba.height() * mRgba.width();
        //
        FeatureDetector detector = FeatureDetector
                .create(FeatureDetector.MSER);
        detector.detect(mGray, keypoint);
        listpoint = keypoint.toList();

        keypoint.release();

        //
        for (int ind = 0; ind < listpoint.size(); ind++) {
            kpoint = listpoint.get(ind);
            rectanx1 = (int) (kpoint.pt.x - 0.5 * kpoint.size);
            rectany1 = (int) (kpoint.pt.y - 0.5 * kpoint.size);
            // rectanx2 = (int) (kpoint.pt.x + 0.5 * kpoint.size);
            // rectany2 = (int) (kpoint.pt.y + 0.5 * kpoint.size);
            rectanx2 = (int) (kpoint.size);
            rectany2 = (int) (kpoint.size);
            if (rectanx1 <= 0)
                rectanx1 = 1;
            if (rectany1 <= 0)
                rectany1 = 1;
            if ((rectanx1 + rectanx2) > mGray.width())
                rectanx2 = mGray.width() - rectanx1;
            if ((rectany1 + rectany2) > mGray.height())
                rectany2 = mGray.height() - rectany1;
            Rect rectant = new Rect(rectanx1, rectany1, rectanx2, rectany2);
            Mat roi = new Mat(mask, rectant);
            roi.setTo(CONTOUR_COLOR);

            roi.release();
        }

        Imgproc.morphologyEx(mask, morbyte, Imgproc.MORPH_DILATE, kernel);

        mask.release();
        kernel.release();

        Imgproc.findContours(morbyte, contour2, hierarchy,
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        for (int ind = 0; ind < contour2.size(); ind++) {
            rectan3 = Imgproc.boundingRect(contour2.get(ind));

            contour2.get(ind).release();

            if (rectan3.area() > 0.5 * imgsize || rectan3.area() < 100
                    || rectan3.width / rectan3.height < 2) {
                Mat roi = new Mat(morbyte, rectan3);
                roi.setTo(zeos);

                roi.release();
            } else
                Imgproc.rectangle(mRgba, rectan3.br(), rectan3.tl(),
                        CONTOUR_COLOR);

        }

        hierarchy.release();
        morbyte.release();

    }
}
