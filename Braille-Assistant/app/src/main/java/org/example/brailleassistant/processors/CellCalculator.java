package org.example.brailleassistant.processors;

import org.example.brailleassistant.utils.BraillePipeline;

import java.util.ArrayList;
import java.util.Collections;

public class CellCalculator extends IProcessor {

    public CellCalculator(String processorId) {
        super(processorId);
    }

    @Override
    public void Execute(BraillePipeline braillePipeline) {

        short[] brailleCellRowValues = braillePipeline.getBrailleCellRowValues();
        short[] internalBrailleCellRows = calculateInternalBrailleCellRows(brailleCellRowValues);
        double[][] brailleDotLocations = braillePipeline.isRotationCorrectionActive() ? braillePipeline.getRotatedBrailleDotLocations() : braillePipeline.getBrailleDotLocations();
        ArrayList[] brailleLineDotLocations = classifyDotsIntoBrailleRows(brailleCellRowValues, internalBrailleCellRows, brailleDotLocations);

        braillePipeline.setBrailleCellValues(extractBrailleCells(braillePipeline.getBrailleCellColumnValues(), brailleLineDotLocations));
    }

    // split braille rows in to three separate sections
    private short[] calculateInternalBrailleCellRows(short[] brailleCellLines) {

        short[] internalBrailleCellRows = new short[(brailleCellLines.length / 2) * 4];

        short brailleRow = 0;

        for (short i = 0; i < brailleCellLines.length; i+=2) {

            short top = brailleCellLines[i];
            short bottom = brailleCellLines[i + 1];
            int rowHeight = (bottom - top) / 3;

            internalBrailleCellRows[brailleRow] = top;
            internalBrailleCellRows[brailleRow + 1] = (short) (top + rowHeight);
            internalBrailleCellRows[brailleRow + 2] = (short) (top + (2*rowHeight));
            internalBrailleCellRows[brailleRow + 3] = bottom;

            brailleRow += 4;
        }

        return internalBrailleCellRows;

    }

    // assign dots in to row 1, 2 or 3 for each braille line
    private ArrayList[] classifyDotsIntoBrailleRows(short[] brailleCellLines, short[] internalBrailleCellRows, double[][] brailleDotLocations) {

        ArrayList<Short>[] brailleLineDotLocations = new ArrayList[(brailleCellLines.length / 2) * 3];

        for (int i = 0; i < brailleLineDotLocations.length; i++) {
            brailleLineDotLocations[i] = new ArrayList<Short>();
        }

        short currentBrailleLine = 0;
        short currentBrailleLinePtr = 0;
        short internalBrailleCellRowPtr = 0;

        for(int i = 0; i < brailleDotLocations.length; i++) {

            short brailleDotRow = (short) brailleDotLocations[i][1];

            if (brailleDotRow > internalBrailleCellRows[internalBrailleCellRowPtr] && brailleDotRow < internalBrailleCellRows[internalBrailleCellRowPtr + 3]) {

                if (brailleDotRow > internalBrailleCellRows[internalBrailleCellRowPtr] && brailleDotRow < internalBrailleCellRows[internalBrailleCellRowPtr + 1]) {
                    brailleLineDotLocations[currentBrailleLinePtr].add((short)brailleDotLocations[i][0]);
                } else if (brailleDotRow > internalBrailleCellRows[internalBrailleCellRowPtr + 1] && brailleDotRow < internalBrailleCellRows[internalBrailleCellRowPtr + 2]) {
                    brailleLineDotLocations[currentBrailleLinePtr + 1].add((short)brailleDotLocations[i][0]);
                } else if (brailleDotRow > internalBrailleCellRows[internalBrailleCellRowPtr + 2] && brailleDotRow < internalBrailleCellRows[internalBrailleCellRowPtr + 3]) {
                    brailleLineDotLocations[currentBrailleLinePtr + 2].add((short)brailleDotLocations[i][0]);
                }

            } else if (currentBrailleLine == (brailleCellLines.length / 2) - 1) {

                break;

            } else if (brailleDotRow > internalBrailleCellRows[internalBrailleCellRowPtr + 4] && brailleDotRow < internalBrailleCellRows[internalBrailleCellRowPtr + 4 + 3]) {

                currentBrailleLine++;
                currentBrailleLinePtr += 3;
                internalBrailleCellRowPtr += 4;

                if (brailleDotRow > internalBrailleCellRows[internalBrailleCellRowPtr] && brailleDotRow < internalBrailleCellRows[internalBrailleCellRowPtr + 1]) {
                    brailleLineDotLocations[currentBrailleLinePtr].add((short)brailleDotLocations[i][0]);
                } else if (brailleDotRow > internalBrailleCellRows[internalBrailleCellRowPtr + 1] && brailleDotRow < internalBrailleCellRows[internalBrailleCellRowPtr + 2]) {
                    brailleLineDotLocations[currentBrailleLinePtr + 1].add((short)brailleDotLocations[i][0]);
                } else if (brailleDotRow > internalBrailleCellRows[internalBrailleCellRowPtr + 2] && brailleDotRow < internalBrailleCellRows[internalBrailleCellRowPtr + 3]) {
                    brailleLineDotLocations[currentBrailleLinePtr + 2].add((short)brailleDotLocations[i][0]);
                }

            }
        }


        for (int i = 0; i < brailleLineDotLocations.length; i++) {
            Collections.sort(brailleLineDotLocations[i]);
        }

        return brailleLineDotLocations;

    }

    // find braille cell value for each cell
    private short[][] extractBrailleCells(short[] brailleCellColumns, ArrayList[] brailleLineDotLocations) {

        // 2D Array - Number of Braille lines x Number of Braille Cells per Line
        short[][] brailleCells = new short[brailleLineDotLocations.length / 3][brailleCellColumns.length / 2];

        short currentBrailleLine = 0;
        short startingDot, lowerBound, upperBound, intermediateBound;

        for (int brailleLine = 0; brailleLine < brailleLineDotLocations.length; brailleLine += 3) {

            for (int brailleRow = 0; brailleRow < 3; brailleRow++) {

                startingDot = 0;

                for (int brailleCell = 0; brailleCell < brailleCellColumns.length; brailleCell += 2) {

                    lowerBound = brailleCellColumns[brailleCell];
                    upperBound = brailleCellColumns[brailleCell + 1];
                    intermediateBound = (short) ((lowerBound + upperBound) / 2);

                    for (int brailleDot = startingDot; brailleDot < brailleLineDotLocations[brailleLine + brailleRow].size(); brailleDot++) {

                        Short x = (Short) brailleLineDotLocations[brailleLine + brailleRow].get(brailleDot);

                        if (x > lowerBound && x < upperBound) {
                            if (x <= intermediateBound) {
                                brailleCells[currentBrailleLine][brailleCell / 2] |= (short) Math.pow(2, brailleRow);
                            } else {
                                brailleCells[currentBrailleLine][brailleCell / 2] |= (short) Math.pow(2, (brailleRow + 3));
                            }

                        } else if (x > upperBound) {

                            break;

                        }

                        startingDot++;

                    }

                }


            }

            currentBrailleLine++;
        }

        return brailleCells;

    }

}
