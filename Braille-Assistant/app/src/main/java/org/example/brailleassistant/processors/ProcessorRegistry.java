package org.example.brailleassistant.processors;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.example.brailleassistant.utils.BraillePipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ProcessorRegistry {

    private ArrayList<IProcessor> processors;

    public ProcessorRegistry() {
        initialiseProcessors();
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
        processors = new ArrayList<IProcessor>();
        processors.add(new ResizeImage("ResizeImage"));
        processors.add(new Filter("Filter"));
        processors.add(new RotationCorrection("RotationCorrection"));
        processors.add(new CellFinder("CellFinder"));
        processors.add(new CellCalculator("CellCalculator"));
        processors.add(new CellParser("CellParser"));
    }
}
