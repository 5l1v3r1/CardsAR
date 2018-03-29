package com.wear.cardsar;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
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

    //@RequiresApi(api = Build.VERSION_CODES.N)
    public void detectEdges(Mat frame, Mat output){
        if (frame == null) return;

        Mat edges = new Mat(frame.size(), CvType.CV_8UC1);
        Mat blur = new Mat(frame.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(frame, edges, Imgproc.COLOR_RGB2GRAY, 4);
        //Imgproc.Canny(edges, edges, 80, 100);
        Size s=new Size();
        s.height=5;
        s.width=5;

        Imgproc.GaussianBlur(edges,blur,s,0);
        int w=frame.width();
        int h=frame.height();
        //rotateNinety(edges, true);
        int w2=frame.width()/2;
        int h100=frame.height()/100;


        int totalBytes = (int)(edges.total() * edges.channels());
        byte buff[] = new byte[totalBytes];
        int px=edges.get(h100, w2, buff);
        int threshlevel=px+60;
        Mat threshimg=new Mat();
        Imgproc.threshold(blur, threshimg,threshlevel,255,Imgproc.THRESH_BINARY);
        Mat hierarchy = new Mat();
        java.util.List<org.opencv.core.MatOfPoint> contours = new java.util.ArrayList<>();
        Imgproc.findContours(threshimg,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

        java.util.List<org.opencv.core.MatOfPoint> srtcontours = new java.util.ArrayList<>();
        Sortct comparator= new Sortct(contours);

        Mat srthier=new Mat();
        hierarchy.copyTo(srthier);
        Integer[] indexes=comparator.createIndexArray();
        Arrays.sort(indexes,comparator);
        int cntiscard[]=new int[indexes.length];
        for (int i=0;i<indexes.length;i++){
            srtcontours.add(contours.get(indexes[i]));
            srthier.put(0,i,hierarchy.get(0,i));
            cntiscard[i]=0;
        }
        for (int i=0;i<srtcontours.size();i++){
            double size=Imgproc.contourArea(srtcontours.get(i));
            MatOfPoint2f a= new MatOfPoint2f(srtcontours.get(i).toArray());
            double peri = Imgproc.arcLength(a,true);
            MatOfPoint2f approx=new MatOfPoint2f();

            Imgproc.approxPolyDP(a,approx,peri*0.01,true);
            int tb = (int)(srthier.total() * srthier.channels());
            byte buf[] = new byte[tb];
            //int pix=edges.get(h100, w2, buff);
            if (size<120000 && size>25000 && approx.toArray().length==4 && srthier.get(i,3,buf)==-1){
                cntiscard[i]=1;
            }
            approx.release();
            a.release();
        }
        for (int i=0;i<srtcontours.size();i++){
            if (cntiscard[i]>0){
                Scalar cl=new Scalar(255,0,0);

                List<MatOfPoint> tmplist=new ArrayList();
                tmplist.add(srtcontours.get(i));
                Imgproc.drawContours(frame,tmplist,-1, cl, 2);

            }
        }

        frame.copyTo(output);

        threshimg.release();
        blur.release();
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
