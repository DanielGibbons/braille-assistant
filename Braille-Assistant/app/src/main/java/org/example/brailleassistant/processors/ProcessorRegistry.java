package org.example.brailleassistant.processors;


import android.content.Context;

import org.example.brailleassistant.utils.BraillePipeline;
import org.example.brailleassistant.utils.CellParserConfig;

import java.util.ArrayList;


public class ProcessorRegistry {

    private ArrayList<IProcessor> processors;
    private CellParserConfig cellParserConfig;

    public ProcessorRegistry(Context context) {
        initialiseProcessors();
        cellParserConfig = new CellParserConfig(context);
    }

    public void executeProcessors(BraillePipeline braillePipeline) {

        for (IProcessor processor: processors) {
            if (processor.getProcessorId().equals("CellCalculator") && !braillePipeline.isCellsValid()) {
                break;
            }
            processor.Execute(braillePipeline);
        }
    }

    private void initialiseProcessors() {
        processors = new ArrayList<>();
        processors.add(new ResizeImage("ResizeImage"));
        processors.add(new Filter("Filter"));
        processors.add(new RotationCorrection("RotationCorrection"));
        processors.add(new CellFinder("CellFinder"));
        processors.add(new CellCalculator("CellCalculator"));
        processors.add(new CellParser("CellParser"));
    }
}
