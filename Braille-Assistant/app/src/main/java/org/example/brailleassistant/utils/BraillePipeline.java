package org.example.brailleassistant.utils;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class BraillePipeline {

    // Toggles
    private boolean isRotationCorrectionActive;

    // Original
    private Mat originalBrailleImage;

    // Filter
    private Mat filteredBrailleImage;
    private double[][] brailleDotLocations;

    // Rotation Correction
    private Mat rotatedFilteredBrailleImage;
    private double[][] rotatedBrailleDotLocations;
    private double rotation;
    private Point rotationCentre;

    // Cell Finder
    private short[] brailleCellRowValues;
    private short [] brailleCellColumnValues;
    private boolean isCellsValid;

    // Cell Calculator
    private short[][] brailleCellValues;

    // Cell Parser
    private String [][] brailleCellTranslation;

    // Getters and Setters
    public Mat getOriginalBrailleImage() {
        return originalBrailleImage;
    }

    public void setOriginalBrailleImage(Mat originalBrailleImage) {
        this.originalBrailleImage = originalBrailleImage;
    }

    public boolean isRotationCorrectionActive() {
        return isRotationCorrectionActive;
    }

    public void setRotationCorrectionActive(boolean rotationCorrectionActive) {
        isRotationCorrectionActive = rotationCorrectionActive;
    }

    public Mat getFilteredBrailleImage() {
        return filteredBrailleImage;
    }

    public void setFilteredBrailleImage(Mat filteredBrailleImage) {
        this.filteredBrailleImage = filteredBrailleImage;
    }

    public double[][] getBrailleDotLocations() {
        return brailleDotLocations;
    }

    public void setBrailleDotLocations(double[][] brailleDotLocations) {
        this.brailleDotLocations = brailleDotLocations;
    }

    public Mat getRotatedFilteredBrailleImage() {
        return rotatedFilteredBrailleImage;
    }

    public void setRotatedFilteredBrailleImage(Mat rotatedFilteredBrailleImage) {
        this.rotatedFilteredBrailleImage = rotatedFilteredBrailleImage;
    }

    public double[][] getRotatedBrailleDotLocations() {
        return rotatedBrailleDotLocations;
    }

    public void setRotatedBrailleDotLocations(double[][] rotatedBrailleDotLocations) {
        this.rotatedBrailleDotLocations = rotatedBrailleDotLocations;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public Point getRotationCentre() {
        return rotationCentre;
    }

    public void setRotationCentre(Point rotationCentre) {
        this.rotationCentre = rotationCentre;
    }

    public short[] getBrailleCellRowValues() {
        return brailleCellRowValues;
    }

    public void setBrailleCellRowValues(short[] brailleCellRowValues) {
        this.brailleCellRowValues = brailleCellRowValues;
    }

    public short[] getBrailleCellColumnValues() {
        return brailleCellColumnValues;
    }

    public void setBrailleCellColumnValues(short[] brailleCellColumnValues) {
        this.brailleCellColumnValues = brailleCellColumnValues;
    }

    public boolean isCellsValid() {
        return isCellsValid;
    }

    public void setCellsValid(boolean cellsValid) {
        isCellsValid = cellsValid;
    }

    public short[][] getBrailleCellValues() {
        return brailleCellValues;
    }

    public void setBrailleCellValues(short[][] brailleCellValues) {
        this.brailleCellValues = brailleCellValues;
    }

    public String[][] getBrailleCellTranslation() {
        return brailleCellTranslation;
    }

    public void setBrailleCellTranslation(String[][] brailleCellTranslation) {
        this.brailleCellTranslation = brailleCellTranslation;
    }
}
