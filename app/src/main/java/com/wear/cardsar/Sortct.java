package com.wear.cardsar;

import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.Comparator;
import java.util.List;

/**
 * Created by larsoe4 on 3/21/2018.
 */
public class Sortct implements Comparator<Integer>
{
    // Used for sorting in ascending order of
    // roll number
    private List<MatOfPoint> arr;
    public Sortct(List<MatOfPoint> array)
    {
        this.arr = array;
    }
    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[arr.size()];
        for (int i = 0; i < arr.size(); i++)
        {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }
    public int compare(Integer a, Integer b)
    {
        Double d=Imgproc.contourArea(arr.get(a))-Imgproc.contourArea(arr.get(b));
        if (d>0) return 1;
        if (d<0) return -1;
        return 0;
    }
}
