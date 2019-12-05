package org.example.brailleassistant.processors;

import org.example.brailleassistant.utils.BraillePipeline;

abstract public class IProcessor {

    private String processorId;

    public IProcessor(String processorId) {
        this.processorId = processorId;
    }

    public String getProcessorId() {
        return processorId;
    }

    abstract public void Execute(BraillePipeline braillePipeline);

}
