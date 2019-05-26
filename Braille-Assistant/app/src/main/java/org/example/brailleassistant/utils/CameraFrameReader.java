package org.example.brailleassistant.utils;

import android.media.Image;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.cvtColor;


public class CameraFrameReader implements Runnable {

    private final Image mImage;
    private final OnFrameEventListener mListener;

    public interface OnFrameEventListener {
        void onFrameEvent(Mat image);
    }

    public CameraFrameReader(Image image, OnFrameEventListener listener) {
        this.mImage = image;
        this.mListener = listener;
    }

    @Override
    public void run() {

        // https://answers.opencv.org/question/61628/android-camera2-yuv-to-rgb-conversion-turns-out-green/
        byte[] nv21;
        ByteBuffer yBuffer = mImage.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = mImage.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = mImage.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv21 = new byte[ySize + uSize + vSize];

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // create NV21 mat
        final Mat image = cvtYUV2Mat(nv21);

        this.mImage.close();// don't forget to close


        if (this.mListener != null) {

            mListener.onFrameEvent(image);

        }

    }

    public Mat cvtYUV2Mat(byte[] data) {
        // create mat
        Mat mYuv = new Mat(this.mImage.getHeight() + this.mImage.getHeight() / 2, this.mImage.getWidth(), CV_8UC1);
        // transfer byte array
        mYuv.put(0, 0, data);
        // create RGB mat
        Mat RGBImage = new Mat();
        // convert colour space
        cvtColor(mYuv, RGBImage, Imgproc.COLOR_YUV2RGB_NV21, 3);
        return RGBImage;

    }

}

