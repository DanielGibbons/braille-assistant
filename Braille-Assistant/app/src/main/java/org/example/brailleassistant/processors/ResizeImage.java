package org.example.brailleassistant.processors;

import org.example.brailleassistant.utils.BraillePipeline;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ResizeImage extends IProcessor {

    public ResizeImage(String processorId) {
        super(processorId);
    }

    @Override
    public void Execute(BraillePipeline braillePipeline) {

        // resize camera image
        Mat brailleImageResized = new Mat();
        Mat brailleImage = braillePipeline.getOriginalBrailleImage();
        Size newSize = new Size(brailleImage.width() * 0.75, brailleImage.height() * 0.75);
        Imgproc.resize(brailleImage, brailleImageResized, newSize);
        braillePipeline.setOriginalBrailleImage(brailleImageResized);

    }
}
