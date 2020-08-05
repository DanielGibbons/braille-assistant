package org.example.brailleassistant.processors;

import org.example.brailleassistant.utils.BraillePipeline;
import org.example.brailleassistant.utils.LibLouisWrapper;


public class CellParser extends IProcessor {

    public CellParser(String processorId) {
        super(processorId);
    }

    @Override
    public void Execute(BraillePipeline braillePipeline) {

        byte[][] brailleCellValues = braillePipeline.getBrailleCellValues();
        String[][] translatedBraille = new String[brailleCellValues.length][brailleCellValues[0].length];
        int row = 0;

        for(byte[] brailleRow: brailleCellValues) {
            String translation = LibLouisWrapper.backTranslate(brailleRow, "en-gb-g1.utb");
            if (translation != null) {
                String[] translationSep = translation.split("(?!^)");
                System.arraycopy(translationSep,0, translatedBraille[row], 0, translationSep.length);
            }
            row++;
        }
        braillePipeline.setBrailleCellTranslation(translatedBraille);

    }

}
