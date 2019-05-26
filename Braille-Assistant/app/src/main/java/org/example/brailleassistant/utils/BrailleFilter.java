package org.example.brailleassistant.utils;


import org.opencv.core.Core;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import java.util.List;

import static java.util.Arrays.sort;


public class BrailleFilter {

    public enum FilterType_t {
        SingleSided,
        DoubleSided
    }

    static public class BrailleFilterOutput_t {

        public Mat brailleImage;
        public double[][] brailleDotLocations;

        public BrailleFilterOutput_t(Mat brailleImage, double[][] brailleDotLocations) {
            this.brailleImage = brailleImage;
            this.brailleDotLocations = brailleDotLocations;
        }

    }

    private FilterType_t mFilterType;
    private CLAHE mClaheKernel;
    private Mat mErodeKernel;
    private Mat mDilateKernel;

    private Mat mThresholdImage;
    private Mat mErodedImage;
    private Mat mDilatedImage;
    private Mat mLabels;
    private Mat mStats;
    private Mat mCentroids;

    public BrailleFilter() {

        // Initialize CLAHE Kernel
        mClaheKernel = Imgproc.createCLAHE();
        mClaheKernel.setClipLimit(3);
        Size tileGridSize = new Size(8,8);
        mClaheKernel.setTilesGridSize(tileGridSize);

        // Initialize Erode and Dilate Kernels
        Size erodeKernelSize = new Size(3, 3);
        Size dilateKernelSize = new Size(6, 6);
        mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, erodeKernelSize);
        mDilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, dilateKernelSize);

        mThresholdImage = new Mat();
        mErodedImage = new Mat();
        mDilatedImage = new Mat();
        mLabels = new Mat();
        mStats = new Mat();
        mCentroids = new Mat();
    }


    public void setFilterType(FilterType_t filterType) {
        mFilterType = filterType;
    }

    public BrailleFilterOutput_t extractBrailleDots(Mat brailleImage) {

        // perform CLAHE
        Mat lightnessEqualizedImage = this.applyLocalLightnessEqualization(brailleImage);
        // perform GHE
        Mat globallyEqualizedImage = this.applyGlobalHistogramEqualization(lightnessEqualizedImage);
        // threshold image
        BrailleFilterOutput_t filterOutput = this.applyThresholding(globallyEqualizedImage);
        // release memory
        lightnessEqualizedImage.release();
        globallyEqualizedImage.release();
        return filterOutput;

    }

    private Mat applyLocalLightnessEqualization(Mat inputImage) {

        // Convert to LAB colour space and split channels
        Mat labImage = new Mat();
        Imgproc.cvtColor(inputImage, labImage, Imgproc.COLOR_RGB2Lab);
        List<Mat> labChannels = new ArrayList<>();
        Core.split(labImage, labChannels);

        // Apply CLAHE to L-channel and merge channels together
        Mat adjustedLChannel = new Mat();
        mClaheKernel.apply(labChannels.get(0), adjustedLChannel);
        adjustedLChannel.copyTo(labChannels.get(0));
        Core.merge(labChannels, labImage);

        // Convert to BGR colour space
        Mat bgrImage = new Mat();
        Mat grayImage = new Mat();
        Imgproc.cvtColor(labImage, bgrImage, Imgproc.COLOR_Lab2BGR);
        Imgproc.cvtColor(bgrImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        bgrImage.release();

        return grayImage;

    }

    private Mat applyGlobalHistogramEqualization(Mat inputImage) {

        // Apply global histogram equalization and blur
        Mat equalizedImage = new Mat();
        Mat blurredEqualizedImage = new Mat();
        Imgproc.equalizeHist(inputImage, equalizedImage);
        Imgproc.medianBlur(equalizedImage, blurredEqualizedImage, 5);

        return blurredEqualizedImage;
    }

    private BrailleFilterOutput_t applyThresholding(Mat inputImage) {

        // threshold image
        int threshold = calculateThreshold(inputImage);
        Imgproc.threshold(inputImage, mThresholdImage, threshold, 255, Imgproc.THRESH_BINARY_INV);

        // erode noise and then dilate
        Point anchor = new Point(0, 0);
        Imgproc.erode(mThresholdImage, mErodedImage, this.mErodeKernel, anchor,2);
        Imgproc.dilate(mErodedImage, mDilatedImage, this.mDilateKernel, anchor,1);

        // find connected components
        int numLabels = Imgproc.connectedComponentsWithStats(mDilatedImage, mLabels, mStats, mCentroids, 4);
        int componentData[] = new int[5];
        int componentAreas[] = new int[numLabels];
        int totalArea = 0;
        double meanArea;
        mStats.get(0, Imgproc.CC_STAT_AREA, componentAreas);

        // calculate mean area
        for (int i = 1; i < numLabels; i++) { // skip first element (background)
            totalArea += componentAreas[i];
        }
        meanArea = (double) totalArea / (numLabels - 1);

        // colours
        Scalar black = new Scalar(0, 0, 0);
        Scalar white = new Scalar(255, 255, 255);

        // delete smaller elements
        for (int i = 4; i < numLabels; i++) { // skip first element (background)

            mStats.row(i).get(0 ,0, componentData);

            if (componentData[Imgproc.CC_STAT_AREA] < meanArea / 2) {
                Point[] corners = new Point[4];
                corners[0] = new Point(componentData[Imgproc.CC_STAT_LEFT], componentData[Imgproc.CC_STAT_TOP]);
                corners[1] = new Point(componentData[Imgproc.CC_STAT_LEFT] + componentData[Imgproc.CC_STAT_WIDTH], componentData[Imgproc.CC_STAT_TOP]);
                corners[2] = new Point(componentData[Imgproc.CC_STAT_LEFT] + componentData[Imgproc.CC_STAT_WIDTH], componentData[Imgproc.CC_STAT_TOP] + componentData[Imgproc.CC_STAT_HEIGHT]);
                corners[3] = new Point(componentData[Imgproc.CC_STAT_LEFT], componentData[Imgproc.CC_STAT_TOP] + componentData[Imgproc.CC_STAT_HEIGHT]);
                MatOfPoint points = new MatOfPoint(corners);
                Imgproc.fillConvexPoly(mDilatedImage, points, new Scalar(0, 0, 0), 1);

            }

        }

        // find noise free connected components
        numLabels = Imgproc.connectedComponentsWithStats(mDilatedImage, mLabels, mStats, mCentroids, 4);

        double[][] brailleDotLocations = new double[numLabels - 1][2];
        double componentCentre[] = new double[2];

        // replace identified braille dots with circles and extract dot locations
        for (int i = 1; i < numLabels; i++) { // skip first element (background)

            mStats.row(i).get(0, 0, componentData);
            Point[] corners = new Point[4];
            corners[0] = new Point(componentData[Imgproc.CC_STAT_LEFT], componentData[Imgproc.CC_STAT_TOP]);
            corners[1] = new Point(componentData[Imgproc.CC_STAT_LEFT] + componentData[Imgproc.CC_STAT_WIDTH], componentData[Imgproc.CC_STAT_TOP]);
            corners[2] = new Point(componentData[Imgproc.CC_STAT_LEFT] + componentData[Imgproc.CC_STAT_WIDTH], componentData[Imgproc.CC_STAT_TOP] + componentData[Imgproc.CC_STAT_HEIGHT]);
            corners[3] = new Point(componentData[Imgproc.CC_STAT_LEFT], componentData[Imgproc.CC_STAT_TOP] + componentData[Imgproc.CC_STAT_HEIGHT]);

            MatOfPoint points = new MatOfPoint(corners);
            Imgproc.fillConvexPoly(mDilatedImage, points, black, 1);

            mCentroids.row(i).get(0, 0, componentCentre);
            Point centre = new Point(componentCentre[0], componentCentre[1]);
            Imgproc.circle(mDilatedImage, centre, 4, white, 7);

            brailleDotLocations[i-1][0] = componentCentre[0];
            brailleDotLocations[i-1][1] = componentCentre[1];

        }

        // sort dot locations by row order
        sort(brailleDotLocations, new java.util.Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return Double.compare(a[1], b[1]);
            }
        });

        return new BrailleFilterOutput_t(mDilatedImage, brailleDotLocations);

    }

    private int calculateThreshold(Mat inputImage) {

        return 20;

    }
}
