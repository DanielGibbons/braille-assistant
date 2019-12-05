package org.example.brailleassistant.processors;

import org.example.brailleassistant.utils.BraillePipeline;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

import static java.lang.Double.NaN;
import static java.util.Arrays.sort;

public class RotationCorrection extends IProcessor {

    public RotationCorrection(String processorId) {
        super(processorId);
    }

    @Override
    public void Execute(BraillePipeline braillePipeline) {

        if (!braillePipeline.isRotationCorrectionActive()) {
            braillePipeline.setRotationCentre(null);
            return;
        }

        double rotation = findImageRotation(braillePipeline.getBrailleDotLocations());
        if (rotation == 0 || ((Double)rotation).isNaN()) {
            braillePipeline.setRotation(0);
            braillePipeline.setRotationCentre(new Point(0, 0));
            return;
        }
        Mat rotatedBrailleImage = rotateImage(braillePipeline.getFilteredBrailleImage(), rotation);
        braillePipeline.setRotatedFilteredBrailleImage(rotatedBrailleImage);;
        braillePipeline.setRotatedBrailleDotLocations(findNewDotLocations(rotatedBrailleImage));
        braillePipeline.setRotation(rotation);
        Mat originalBrailleImage = braillePipeline.getFilteredBrailleImage();
        braillePipeline.setRotationCentre(new Point(originalBrailleImage.width(), originalBrailleImage.height()));

    }

    // finds new dot locations - these changed due to image rotation
    private double[][] findNewDotLocations(Mat filteredBrailleImage) {

        Mat labels = new Mat();
        Mat stats = new Mat();
        Mat centroids = new Mat();
        int numLabels;

        numLabels = Imgproc.connectedComponentsWithStats(filteredBrailleImage, labels, stats, centroids, 4);

        double[][] brailleDotLocations = new double[numLabels - 1][2];
        double componentCentre[] = new double[2];

        for (int i = 1; i < numLabels; i++) { // skip first element (background)

            centroids.row(i).get(0, 0, componentCentre);
            brailleDotLocations[i - 1][0] = componentCentre[0];
            brailleDotLocations[i - 1][1] = componentCentre[1];
        }

        sort(brailleDotLocations, new java.util.Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return Double.compare(a[1], b[1]);
            }
        });

        return brailleDotLocations;

    }

    // finds rotation between two points from vertical axis
    private double findRotation(int x1, int y1, int x2, int y2) {

        if (y1 == y2 || x1 == x2 || Math.abs(x2 - x1) > Math.abs(y2 - y1)) {
            return 0;
        }

        // sort vertices so y2 > y1
        if (y1 > y2) {
            int temp_x = x1;
            int temp_y = y1;
            x1 = x2;
            y1 = y2;
            x2 = temp_x;
            y2 = temp_y;
        }

        double angle_radians = Math.toRadians(90) - Math.atan(Math.abs(y2 - y1) / Math.abs(x2 - x1));

        double angle_degrees = Math.toDegrees(angle_radians);

        if (x2 < x1){
            angle_degrees = -angle_degrees;
        }

        return angle_degrees;

    }

    // distance squared between two points - pythagoras
    private int findDistanceSquared(int x1, int y1, int x2, int y2) {

        return (int)(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

    }

    // finds nearest braille dot from current braille dot
    private int[] findNearestNeighbour(double[] point, double[][] points) {

        int[] nearestNeighbour = new int[3];
        int distance = 99999999;
        int latest_distance;

        for (int i = 0; i < points.length; i++) {

            if (point[0] != points[i][0] && point[1] != points[i][1]) {

                latest_distance = findDistanceSquared((int)point[0], (int)point[1], (int)points[i][0], (int)points[i][1]);

                if (latest_distance <= distance) {
                    distance = latest_distance;
                    nearestNeighbour[0] = (int)points[i][0];
                    nearestNeighbour[1] = (int)points[i][1];
                }
            }

        }

        nearestNeighbour[2] = distance;

        return  nearestNeighbour;

    }

    // finds an image's rotation based on detected braille dot locations
    private double findImageRotation(double[][] dotLocations) {

        int[] nearestNeighbour;
        double[][] dotRelationship = new double[dotLocations.length][2];

        int numberOfRelevantDotRelationships = 0;

        int numberOfRelevantRotationRelationships = 0;
        double accumulatedRotation = 0;
        double averageRotation;

        // find all valid dot relationships based on dot nearest neighbours
        for (int i = 0; i < dotLocations.length; i++) {

            nearestNeighbour = findNearestNeighbour(dotLocations[i], dotLocations);
            double rotation = findRotation((int)dotLocations[i][0], (int)dotLocations[i][1], nearestNeighbour[0], nearestNeighbour[1]);

            if (Math.abs(rotation) < 15 && rotation != 0) {
                dotRelationship[numberOfRelevantDotRelationships][0] = nearestNeighbour[2];
                dotRelationship[numberOfRelevantDotRelationships][1] = rotation;
                numberOfRelevantDotRelationships++;
            }

        }

        // not a significant number of relationships
        if (numberOfRelevantDotRelationships < 15) {
            return 0;
        }

        double[][] dotRelationshipResized = Arrays.copyOfRange(dotRelationship, 0, numberOfRelevantDotRelationships);

        sort(dotRelationshipResized, new java.util.Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return Double.compare(a[0], b[0]);
            }
        });

        // find mean of all rotations detected in inter-quartile range
        for (int i = (int)(dotRelationshipResized.length * 0.25); i < (int)(dotRelationshipResized.length * 0.75); i++) {

            accumulatedRotation += dotRelationshipResized[i][1];
            numberOfRelevantRotationRelationships++;

        }
        averageRotation = accumulatedRotation / numberOfRelevantRotationRelationships;

        return -averageRotation;

    }

    // rotates an image by specified angle and resizes image canvas so all of image is included
    private Mat rotateImage(Mat filteredBrailleImage, double angle) {

        Mat rotatedImage = new Mat();

        Mat rotationMatrix = Imgproc.getRotationMatrix2D(new Point(filteredBrailleImage.width() / 2, filteredBrailleImage.height() / 2), angle, 1);

        // https://stackoverflow.com/questions/43892506/opencv-python-rotate-image-without-cropping-sides
        double angle_radians = Math.toRadians(angle);
        double sin = Math.sin(angle_radians);
        double cos = Math.cos(angle_radians);
        int bound_w = (int)((filteredBrailleImage.height() * Math.abs(sin)) + (filteredBrailleImage.width() * Math.abs(cos)));
        int bound_h = (int)((filteredBrailleImage.height() * Math.abs(cos)) + (filteredBrailleImage.width() * Math.abs(sin)));

        double[] currentValue = new double[1];

        // adapt rotation matrix
        rotationMatrix.get(0, 2, currentValue);
        rotationMatrix.put(0, 2, currentValue[0] + ((bound_w / 2) - (filteredBrailleImage.width() / 2)));

        rotationMatrix.get(1, 2, currentValue);
        rotationMatrix.put(1, 2, currentValue[0] + ((bound_h / 2) - (filteredBrailleImage.height() / 2)));

        Imgproc.warpAffine(filteredBrailleImage, rotatedImage, rotationMatrix, new Size(bound_w, bound_h));

        return rotatedImage;

    }
}
