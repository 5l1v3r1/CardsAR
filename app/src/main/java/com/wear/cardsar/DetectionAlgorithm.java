package com.wear.cardsar;

import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetectionAlgorithm{

    DetectionAlgorithm(){
        super();
    }

    public void processInput(Mat input, Mat output){

        detectOutlines(input, output);
    }

    public List<DetectedCard> findAllCards(Mat input){
        return null;
    }

    private Mat preprocess(Mat input){
        return  null;
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    private void detectOutlines(Mat input, Mat output){
        if (input == null) return;

        Mat gray = new Mat(input.size(), CvType.CV_8UC1);
        Mat blur = new Mat(input.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_RGB2GRAY);
        //turn the image to a grayscale img with edges being gray
        Mat edges = new Mat(input.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(input, edges, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(edges, edges, 80, 100);



        /*

        Size s=new Size();
        s.height=5;
        s.width=5;

        Imgproc.GaussianBlur(gray,blur,s,0);

        //blur.copyTo(output);

        int w = input.width();
        int h = input.height();
        //rotateNinety(edges, true);
        int w2 = input.width()/2;
        int h100 = input.height()/100;


        int totalBytes = (int)(gray.total() * gray.channels());
        byte buff[] = new byte[totalBytes];
        gray.get(h100, w2, buff);
        int threshlevel = buff[0] + 60;
        //System.out.println("px: " + buff[0] +"jkjkjkj"+ buff[1]);
        //Mat threshimg = new Mat(input.size(), CvType.CV_8UC1);
        Mat invertcolormatrix= new Mat(blur.rows(),blur.cols(), blur.type(), new Scalar(255,255,255));
        */
        //Core.subtract(invertcolormatrix, blur, blur);
        Mat threshimg = new Mat(input.size(), CvType.CV_8UC1);

        //Imgproc.cvtColor(input, threshimg, Imgproc.COLOR_RGB2GRAY, 4);
        //Imgproc.Canny(threshimg, threshimg, 80, 100);
        //Imgproc.adaptiveThreshold(blur, threshimg, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,7, 5);
        //Imgproc.threshold(blur, threshimg, threshlevel,255, Imgproc.THRESH_BINARY);
        //find all contours and their hierarchy
        edges.copyTo(threshimg);
        Mat hierarchy = new Mat(input.size(), CvType.CV_8UC1);
        java.util.List<org.opencv.core.MatOfPoint> contours = new java.util.ArrayList<org.opencv.core.MatOfPoint>();
        Imgproc.findContours(threshimg,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0) );

        // Filter by size
        List<MatOfPoint> filteredContours = new ArrayList<>();
        for (MatOfPoint contour : contours){
            if (Imgproc.contourArea(contour) > 1000){
                filteredContours.add(contour);
            }
        }
        contours = filteredContours;

        java.util.List<org.opencv.core.MatOfPoint> srtcontours = new java.util.ArrayList<org.opencv.core.MatOfPoint>();

        //create an array to store all the sorted hierarchy and contours
        Mat srthier = new Mat(input.size(), hierarchy.type());
        hierarchy.copyTo(srthier);
        int tb = (int)(hierarchy.total() * hierarchy.channels());
        //System.out.println("w "+hierarchy.height()+"jkjkghghghghghghghj---"+hierarchy.width()+ " ddd " + tb);
        //sort all indexes based on contours size
        Sortct comparator = new Sortct(contours);
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes,comparator);

        int cntiscard[] = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++){
            srtcontours.add(contours.get(indexes[i]));

            int buf[] = new int[tb];
            hierarchy.get(0,indexes[i],buf);
            srthier.put(0,i,buf);

            cntiscard[i] = 0;
        }
        System.out.println("BEGIN CONTOURS");
        for (int i = 0; i < srtcontours.size(); i++){
            double size = Imgproc.contourArea(srtcontours.get(i));
            MatOfPoint2f a = new MatOfPoint2f(srtcontours.get(i).toArray());
            double peri = Imgproc.arcLength(a,true);
            MatOfPoint2f approx=new MatOfPoint2f();

            Imgproc.approxPolyDP(a,approx,peri*0.01,true);
            tb = (int)(srthier.total() * srthier.channels());
            int buf[] = new int[tb];

            srthier.get(0,i, buf);

            //System.out.println(srtcontours.size()+"jkjkghghghghghghghj---"+tb + " ddd\n");
            //draw a green box if contours has no parent and has 4 corners
            if (buf[3]==-1 && approx.toArray().length == 4){

                Scalar cl = new Scalar(0,255,0);
                System.out.println("ddyyd\n");
                List<MatOfPoint> tmplist = new ArrayList<>();
                //tmplist.add(srtcontours.get(i));
                MatOfPoint approxf1 = new MatOfPoint();
                approx.convertTo(approxf1, CvType.CV_32S);
                tmplist.add(approxf1);
                Imgproc.drawContours(input,tmplist,-1, cl, 2);
                approxf1.release();
            }
            //int pix=edges.get(h100, w2, buff);
            //check if countour is whin size, has 4 corners and has no parent contour
            if (size<120000 && size>25000 && approx.toArray().length == 4 && buf[3] == -1){
                cntiscard[i] = 1;
            }
            approx.release();
            a.release();
        }
        for (int i = 0; i < srtcontours.size(); i++){
            //draw an extra red box if contours has no parent and has 4 corners
            if (cntiscard[i] == 1){
                Scalar cl = new Scalar(255,0,0);

                List<MatOfPoint> tmplist = new ArrayList<>();
                tmplist.add(srtcontours.get(i));
                Imgproc.drawContours(input,tmplist,-1, cl, 2);
            }

        }


        for (int i = 0; i < srtcontours.size(); i++){
            //srtcontours.get(i).release();
        }
        input.copyTo(output);

        //threshimg.release();

        //blur.release();

        //gray.release();
    }

    public void detectEdges(Mat frame, Mat output){
        if (frame == null) return;

        Mat edges = new Mat(frame.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(frame, edges, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(edges, edges, 80, 100);

        edges.copyTo(output);

        edges.release();


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
