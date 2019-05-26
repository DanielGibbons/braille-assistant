package org.example.brailleassistant.utils;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;


public class BrailleCellFinder {

    public class BrailleCellFinderOutput_t {

        public short[] brailleLines;
        public short[] brailleCellColumns;
        public boolean validity;

        public BrailleCellFinderOutput_t(short[] brailleLines, short[] brailleCellColumns) {
            this.brailleLines = brailleLines;
            this.brailleCellColumns = brailleCellColumns;
            this.validity = false;
        }
    }

    private class EmptyRowsAndColumns_t {

        short[] emptyRows;
        short[] emptyColumns;

        private EmptyRowsAndColumns_t(short[] emptyRows, short[] emptyColumns) {
            this.emptyRows = emptyRows;
            this.emptyColumns = emptyColumns;
        }
    }

    public BrailleCellFinder() {

    }

    public BrailleCellFinderOutput_t calculateBrailleCells(Mat brailleImage) { // BrailleCellFinderOutput_t

        EmptyRowsAndColumns_t empty = this.findEmptyRowsAndColumns(brailleImage);
        short[] brailleLines = this.findBrailleLines(empty.emptyRows);
        short[] brailleCellColumns = this.findBrailleCellColumns(empty.emptyColumns);
        BrailleCellFinderOutput_t cellFinderOutput = new BrailleCellFinderOutput_t(brailleLines, brailleCellColumns);
        if (cellFinderOutput.brailleCellColumns == null || cellFinderOutput.brailleLines == null) {

            cellFinderOutput.validity = false;

        } else {

            this.validateIdentifiedCells(cellFinderOutput);

        }

        return cellFinderOutput;

    }

    private void validateIdentifiedCells(BrailleCellFinderOutput_t identifiedBrailleCells) {

        int numberOfBrailleLines = identifiedBrailleCells.brailleLines.length / 2;
        int[] lineWidths = new int[numberOfBrailleLines];

        if (identifiedBrailleCells.brailleLines.length == 2) {
            // do nothing
        } else if (identifiedBrailleCells.brailleLines.length > 2) {
            for (int i = 0; i < identifiedBrailleCells.brailleLines.length; i+=2) {
                lineWidths[i/2] = identifiedBrailleCells.brailleLines[i + 1] - identifiedBrailleCells.brailleLines[i];
            }
            Arrays.sort(lineWidths);
            int medianWidth = lineWidths[numberOfBrailleLines / 2];
            if((lineWidths[0] < 0.75*medianWidth) || (lineWidths[numberOfBrailleLines - 1] > 1.25*medianWidth)) {
                return;
            }

        } else {
            return;
        }

        int numberOfBrailleColumns = identifiedBrailleCells.brailleCellColumns.length / 2;
        int[] cellWidths = new int[numberOfBrailleColumns];

        if (identifiedBrailleCells.brailleCellColumns.length == 2) {
            // do nothing
        } else if (identifiedBrailleCells.brailleCellColumns.length > 2) {
            for (int i = 0; i < identifiedBrailleCells.brailleCellColumns.length; i+=2) {
                cellWidths[i/2] = identifiedBrailleCells.brailleCellColumns[i + 1] - identifiedBrailleCells.brailleCellColumns[i];
            }
            Arrays.sort(cellWidths);
            int medianWidth = cellWidths[numberOfBrailleColumns / 2];
            if((cellWidths[0] < 0.75*medianWidth) || (cellWidths[numberOfBrailleColumns - 1] > 1.25*medianWidth)) {
                return;
            }
        } else {
            return;
        }

        identifiedBrailleCells.validity = true;

    }

    private EmptyRowsAndColumns_t findEmptyRowsAndColumns(Mat brailleImage) {

        short[] emptyRows = new short[brailleImage.height()];
        short numberOfEmptyRows = 0;
        long rowTotal;

        short[] emptyColumns = new short[brailleImage.width()];
        short numberOfEmptyColumns = 0;
        long columnTotal;

        int size = (int) (brailleImage.total() * brailleImage.channels());
        byte[] temp = new byte[size];
        brailleImage.get(0, 0, temp);

        long rowLimit = (brailleImage.width() * (brailleImage.height() - 1));
        int rowIncrement = brailleImage.width();
        short rowNumber = 0;

        // Empty Rows
        for (int j = 0; j <= rowLimit; j += rowIncrement) {

            rowTotal = 0;
            rowNumber++;


            for (short i = 0; i < brailleImage.width(); i++) {

                rowTotal += temp[j + i];


            }

            if (rowTotal == 0) {

                emptyRows[numberOfEmptyRows] = rowNumber;
                numberOfEmptyRows++;

            }

        }

        // Empty Columns
        for (short i = 0; i < brailleImage.width(); i++) {

            columnTotal = 0;



            for (int j = 0; j <= rowLimit; j += rowIncrement) {

                columnTotal += temp[j + i];


            }

            if (columnTotal == 0) {

                emptyColumns[numberOfEmptyColumns] = i;
                numberOfEmptyColumns++;

            }

        }

        return new EmptyRowsAndColumns_t(Arrays.copyOfRange(emptyRows, 0, numberOfEmptyRows), Arrays.copyOfRange(emptyColumns, 0, numberOfEmptyColumns));

    }

    private short[] findBrailleLines(short[] emptyRows) {

        ArrayList<Short> emptyRowLabelsList = new ArrayList<>();

        // Initialise emptyRowLabel
        short[] emptyRowLabel = new short[2];
        emptyRowLabel[0] = emptyRows[0]; // first empty row
        emptyRowLabel[1] = 0; // number of consecutive empty rows

        for (short i = 1; i < emptyRows.length; i++) {
            if ((emptyRows[i] - emptyRows[i - 1]) == 1) { // consecutive empty rows
                emptyRowLabel[1] += 1;
            } else {
                emptyRowLabelsList.add(emptyRowLabel[0]);
                emptyRowLabelsList.add(emptyRowLabel[1]);
                emptyRowLabel[0] = emptyRows[i];
                emptyRowLabel[1] = 0;
            }
        }
        // add last label if there is a sequence of empty rows at the bottom of the image
        if (emptyRowLabel[1] > 0) {
            emptyRowLabelsList.add(emptyRowLabel[0]);
            emptyRowLabelsList.add(emptyRowLabel[1]);
        }

        // convert to array
        Short[] emptyRowLabelsArray = emptyRowLabelsList.toArray(new Short[emptyRowLabelsList.size()]);

        // calculate mean number of consecutive empty rows
        int meanRowGap = 0;
        for (short i = 1; i < emptyRowLabelsArray.length; i+=2) {
            meanRowGap += emptyRowLabelsArray[i];
        }
        meanRowGap = meanRowGap / (emptyRowLabelsArray.length / 2);

        // find braille lines
        ArrayList<Short> emptyRowLabelsOfInterestList = new ArrayList<>();

        for (short i = 0; i < emptyRowLabelsArray.length; i+=2) {
            if (emptyRowLabelsArray[i + 1] > meanRowGap) {
                emptyRowLabelsOfInterestList.add(emptyRowLabelsArray[i]);
                emptyRowLabelsOfInterestList.add(emptyRowLabelsArray[i + 1]);
            }
        }

        // convert to array
        Short[] emptyRowLabelsOfInterestArray = emptyRowLabelsOfInterestList.toArray(new Short[emptyRowLabelsOfInterestList.size()]);

        if (emptyRowLabelsOfInterestArray.length > 1) {
            short[] brailleLines = new short[emptyRowLabelsOfInterestArray.length - 2];

            for (short i = 0; i < emptyRowLabelsOfInterestArray.length - 2; i += 2) {
                brailleLines[i] = (short) (emptyRowLabelsOfInterestArray[i] + emptyRowLabelsOfInterestArray[i + 1]);
                brailleLines[i + 1] = emptyRowLabelsOfInterestArray[i + 2];
            }

            return brailleLines;
        } else {

            return null;

        }
    }

    private short[] findBrailleCellColumns(short[] emptyColumns) {

        ArrayList<Short> emptyColumnLabelsList = new ArrayList<>();

        // Initialise emptyRowLabel
        short[] emptyColumnLabel = new short[2];
        emptyColumnLabel[0] = emptyColumns[0]; // first empty row
        emptyColumnLabel[1] = 0; // number of consecutive empty rows

        for (short i = 1; i < emptyColumns.length; i++) {
            if ((emptyColumns[i] - emptyColumns[i - 1]) == 1) { // consecutive empty rows
                emptyColumnLabel[1] += 1;
            } else {
                emptyColumnLabelsList.add(emptyColumnLabel[0]);
                emptyColumnLabelsList.add(emptyColumnLabel[1]);
                emptyColumnLabel[0] = emptyColumns[i];
                emptyColumnLabel[1] = 0;
            }
        }
        // add last label if there is a sequence of empty rows at the bottom of the image
        if (emptyColumnLabel[1] > 0) {
            emptyColumnLabelsList.add(emptyColumnLabel[0]);
            emptyColumnLabelsList.add(emptyColumnLabel[1]);
        }

        // convert to array
        Short[] emptyColumnLabelsArray = emptyColumnLabelsList.toArray(new Short[emptyColumnLabelsList.size()]);

        // calculate mean number of consecutive empty rows
        int meanColumnGap = 0;
        for (short i = 1; i < emptyColumnLabelsArray.length; i+=2) {
            meanColumnGap += emptyColumnLabelsArray[i];
        }
        meanColumnGap = meanColumnGap / (emptyColumnLabelsArray.length / 2);

        ArrayList<Short> emptyColumnLabelsOfInterestList = new ArrayList<>();

        for (short i = 0; i < emptyColumnLabelsArray.length; i+=2) {
            if (emptyColumnLabelsArray[i + 1] > meanColumnGap) {
                emptyColumnLabelsOfInterestList.add(emptyColumnLabelsArray[i]);
                emptyColumnLabelsOfInterestList.add(emptyColumnLabelsArray[i + 1]);
            }
        }

        // convert to array
        Short[] emptyColumnLabelsOfInterestArray = emptyColumnLabelsOfInterestList.toArray(new Short[emptyColumnLabelsOfInterestList.size()]);

        if (emptyColumnLabelsOfInterestArray.length > 1) {
            short[] brailleCellColumns = new short[emptyColumnLabelsOfInterestArray.length - 2];

            for (short i = 0; i < emptyColumnLabelsOfInterestArray.length - 2; i += 2) {
                 brailleCellColumns[i] = (short) (emptyColumnLabelsOfInterestArray[i] + emptyColumnLabelsOfInterestArray[i+1]);
                 brailleCellColumns[i+1] = (short) (emptyColumnLabelsOfInterestArray[i+2]);
            }

            return brailleCellColumns;

        } else {

            return null;

        }

    }

}
