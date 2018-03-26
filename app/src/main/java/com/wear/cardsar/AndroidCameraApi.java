package com.wear.cardsar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.concurrent.locks.*;

public class AndroidCameraApi extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    private boolean framePaused = false;
    private Mat lastInputFrame = null;
    Lock lastInputFrameLock;
    private Mat outputFrame = null;
    Lock outputFrameLock;
    private DetectionAlgorithm mAlgorithm;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public AndroidCameraApi() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_android_camera_api);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.texture);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

        mAlgorithm = new DetectionAlgorithm(this);
        mAlgorithm.start();

        lastInputFrameLock = new ReentrantLock();
        outputFrameLock = new ReentrantLock();

        final Button feedToggleButton = findViewById(R.id.btn_togglelivefeed);
        feedToggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                framePaused = !framePaused;
            }
        });

    }

    @Override
    public void onPause()
    {
        mAlgorithm.pauseAlgorithm();
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        mAlgorithm.resumeAlgorithm();
    }

    public void onDestroy() {
        super.onDestroy();

        mAlgorithm.kill();
        try {
            mAlgorithm.join();
        }catch(InterruptedException e){

        }

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public boolean getLastInput(Mat dest){

        lastInputFrameLock.lock();
        try {
            if (lastInputFrame == null){
                return false;
            }else {
                lastInputFrame.copyTo(dest);
            }

        }finally{
            lastInputFrameLock.unlock();
        }

        return true;
    }

    private void setLastInputFrame(Mat frame){

        lastInputFrameLock.lock();
        try {
            if (lastInputFrame != null){
                lastInputFrame.release();
            }else {
                lastInputFrame = new Mat();
            }
            frame.copyTo(lastInputFrame);

            System.out.println("lastInputFrame now a Mat: " + lastInputFrame.toString());

        }finally{
            lastInputFrameLock.unlock();
        }

    }

    public void setOutputFrame(Mat frame){

        outputFrameLock.lock();
        try {
            if (outputFrame != null ){
                outputFrame.release();
            }else{
                outputFrame = new Mat();
            }

            frame.copyTo(outputFrame);
            System.out.println("outputFrame now a Mat: " + outputFrame.toString());

        }finally{
            outputFrameLock.unlock();
        }

    }

    public boolean getOutputFrame(Mat frame){
        outputFrameLock.lock();
        try {
            if (outputFrame == null) return false;

            outputFrame.copyTo(frame);

        }finally{
            outputFrameLock.unlock();
        }

        return true;
    }

    @Override
    public Mat onCameraFrame(Mat inputMat) {

        if (inputMat == null){
            return null;
        }

        if (!framePaused){
            setLastInputFrame(inputMat);
        }

        getOutputFrame(inputMat);

        return inputMat;

        /*

        System.out.println("Input at beginning: " + inputMat.toString());

        setLastInputFrame(inputMat);

        if (framePaused){
            if (outputFrame == null){
                return inputMat;
            }else {
                outputFrame.copyTo(inputMat);
                return inputMat;
            }
        }

        Mat output = new Mat();

        DetectionAlgorithm.detectEdges(inputMat, output);

        System.out.println("Output is frame with size " + output.size().toString());

        setOutputFrame(output);

        if (output.size().area() <= 0.0f){
            System.out.println("bad output, returning input");

            System.out.println("Input at end: " + inputMat.toString());
        }else{
            outputFrame.copyTo(inputMat);

        }
        output.release();

        return inputMat;

        */
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }
}