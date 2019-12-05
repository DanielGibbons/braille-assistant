package org.example.brailleassistant.processors;

import org.example.brailleassistant.utils.BraillePipeline;
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

public class Filter extends IProcessor {

    private CLAHE claheKernel;
    private Mat erodeKernel;
    private Mat dilateKernel;

    private Mat thresholdImage;
    private Mat erodedImage;
    private Mat dilatedImage;
    private Mat labels;
    private Mat stats;
    private Mat centroids;

    public Filter(String processorId) {
        super(processorId);
        initialiseFilter();
    }

    @Override
    public void Execute(BraillePipeline braillePipeline) {

        Mat lightnessEqualizedImage = applyLocalLightnessEqualization(braillePipeline.getOriginalBrailleImage());
        Mat globallyEqualizedImage = applyGlobalHistogramEqualization(lightnessEqualizedImage);
        applyThresholding(braillePipeline, globallyEqualizedImage);

    }

    private void initialiseFilter() {
        // Initialize CLAHE Kernel
        claheKernel = Imgproc.createCLAHE();
        claheKernel.setClipLimit(3);
        claheKernel.setTilesGridSize(new Size(8,8));

        // Initialize Erode and Dilate Kernels
        erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        dilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(6, 6));

        thresholdImage = new Mat();
        erodedImage = new Mat();
        dilatedImage = new Mat();
        labels = new Mat();
        stats = new Mat();
        centroids = new Mat();
    }

    private Mat applyLocalLightnessEqualization(Mat inputImage) {

        // Convert to LAB colour space and split channels
        Mat labImage = new Mat();
        Imgproc.cvtColor(inputImage, labImage, Imgproc.COLOR_RGB2Lab);
        List<Mat> labChannels = new ArrayList<>();
        Core.split(labImage, labChannels);

        // Apply CLAHE to L-channel and merge channels together
        Mat adjustedLChannel = new Mat();
        claheKernel.apply(labChannels.get(0), adjustedLChannel);
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

    private void applyThresholding(BraillePipeline braillePipeline, Mat inputImage) {

        // threshold image
        int threshold = calculateThreshold(inputImage);
        Imgproc.threshold(inputImage, thresholdImage, threshold, 255, Imgproc.THRESH_BINARY_INV);

        // erode noise and then dilate
        Point anchor = new Point(0, 0);
        Imgproc.erode(thresholdImage, erodedImage, erodeKernel, anchor,2);
        Imgproc.dilate(erodedImage, dilatedImage, dilateKernel, anchor,1);

        // find connected components
        int numLabels = Imgproc.connectedComponentsWithStats(dilatedImage, labels, stats, centroids, 4);
        int componentData[] = new int[5];
        int componentAreas[] = new int[numLabels];
        int totalArea = 0;
        double meanArea;
        stats.get(0, Imgproc.CC_STAT_AREA, componentAreas);

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

            stats.row(i).get(0 ,0, componentData);

            if (componentData[Imgproc.CC_STAT_AREA] < meanArea / 2) {
                Point[] corners = new Point[4];
                corners[0] = new Point(componentData[Imgproc.CC_STAT_LEFT], componentData[Imgproc.CC_STAT_TOP]);
                corners[1] = new Point(componentData[Imgproc.CC_STAT_LEFT] + componentData[Imgproc.CC_STAT_WIDTH], componentData[Imgproc.CC_STAT_TOP]);
                corners[2] = new Point(componentData[Imgproc.CC_STAT_LEFT] + componentData[Imgproc.CC_STAT_WIDTH], componentData[Imgproc.CC_STAT_TOP] + componentData[Imgproc.CC_STAT_HEIGHT]);
                corners[3] = new Point(componentData[Imgproc.CC_STAT_LEFT], componentData[Imgproc.CC_STAT_TOP] + componentData[Imgproc.CC_STAT_HEIGHT]);
                MatOfPoint points = new MatOfPoint(corners);
                Imgproc.fillConvexPoly(dilatedImage, points, new Scalar(0, 0, 0), 1);

            }

        }

        // find noise free connected components
        numLabels = Imgproc.connectedComponentsWithStats(dilatedImage, labels, stats, centroids, 4);

        double[][] brailleDotLocations = new double[numLabels - 1][2];
        double componentCentre[] = new double[2];

        // replace identified braille dots with circles and extract dot locations
        for (int i = 1; i < numLabels; i++) { // skip first element (background)

            stats.row(i).get(0, 0, componentData);
            Point[] corners = new Point[4];
            corners[0] = new Point(componentData[Imgproc.CC_STAT_LEFT], componentData[Imgproc.CC_STAT_TOP]);
            corners[1] = new Point(componentData[Imgproc.CC_STAT_LEFT] + componentData[Imgproc.CC_STAT_WIDTH], componentData[Imgproc.CC_STAT_TOP]);
            corners[2] = new Point(componentData[Imgproc.CC_STAT_LEFT] + componentData[Imgproc.CC_STAT_WIDTH], componentData[Imgproc.CC_STAT_TOP] + componentData[Imgproc.CC_STAT_HEIGHT]);
            corners[3] = new Point(componentData[Imgproc.CC_STAT_LEFT], componentData[Imgproc.CC_STAT_TOP] + componentData[Imgproc.CC_STAT_HEIGHT]);

            MatOfPoint points = new MatOfPoint(corners);
            Imgproc.fillConvexPoly(dilatedImage, points, black, 1);

            centroids.row(i).get(0, 0, componentCentre);
            Point centre = new Point(componentCentre[0], componentCentre[1]);
            Imgproc.circle(dilatedImage, centre, 4, white, 7);

            brailleDotLocations[i-1][0] = componentCentre[0];
            brailleDotLocations[i-1][1] = componentCentre[1];

        }

        // sort dot locations by row order
        sort(brailleDotLocations, new java.util.Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return Double.compare(a[1], b[1]);
            }
        });

        braillePipeline.setFilteredBrailleImage(dilatedImage);
        braillePipeline.setBrailleDotLocations(brailleDotLocations);

    }

    private int calculateThreshold(Mat inputImage) {

        return 20;

    }
}
