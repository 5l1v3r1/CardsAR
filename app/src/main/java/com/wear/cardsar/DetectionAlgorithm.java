package com.wear.cardsar;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetectionAlgorithm extends Thread {

    private CameraView mCameraAPI;
    private boolean runAlgorithm;
    private boolean killed;

    DetectionAlgorithm(CameraView cameraAPI){
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void detectEdges(Mat frame, Mat output){
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
        for (int i=0;i<srtcontours.size();i++){
            srtcontours.get(i).release();
        }
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
}
