package org.example.brailleassistant.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.widget.ImageView;

import org.opencv.core.Point;


public class CameraOverlayRenderer {

    private ImageView mCameraOverlayView;
    private Bitmap mCameraOverlayBitmap;
    private Canvas mCameraOverlayCanvas;
    private Paint mPaintBox;
    private Paint mPaintText;
    private Paint mPaintCrosshair;
    private Matrix mMatrix;
    private Matrix mMatrixScale;
    private Boolean mIsOverlayActive = false;
    private int mCameraPreviewXCentre;
    private int mCameraPreviewYCentre;
    private int mResizedCameraPreviewWidth;


    public CameraOverlayRenderer(ImageView imageView) {

        // Cell box paint
        mCameraOverlayView = imageView;
        mPaintBox = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBox.setColor(Color.BLUE);
        mPaintBox.setStyle(Paint.Style.STROKE);
        mPaintBox.setStrokeWidth(2);

        // Cell translation paint
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.RED);
        mPaintText.setTextSize(120);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setStrokeWidth(4);

        // Crosshair paint
        mPaintCrosshair = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCrosshair.setColor(Color.GREEN);
        mPaintCrosshair.setTextSize(120);
        mPaintCrosshair.setStyle(Paint.Style.FILL);
        mPaintCrosshair.setStrokeWidth(1);

        // Initialise camera overlay bitmap and canvas once it has been established in the layout
        mCameraOverlayView.post(new Runnable() {
            @Override
            public void run() {

                // create canvas to draw on image view
                mCameraOverlayBitmap = Bitmap.createBitmap(mCameraOverlayView.getWidth(), mCameraOverlayView.getHeight(), Bitmap.Config.ARGB_4444);
                mCameraOverlayCanvas = new Canvas(mCameraOverlayBitmap);

                // scaling matrix to account for image resize
                mMatrixScale = new Matrix();
                mMatrixScale.setScale((float) 4 / 3,(float) 4 / 3, 0, 0);

                // draw alignment crosshair
                drawCrosshair();

            }
        });
    }

    // mapping matrix based on size of camera preview resolution chosen which depends on overlay size (preview surface size)
    public void setMappingMatrix(android.util.Size previewSize) {

        mMatrix = new Matrix();
        mMatrix.setTranslate((float)(mCameraOverlayView.getWidth() - previewSize.getHeight()) / 2, (float)(mCameraOverlayView.getHeight()-previewSize.getWidth()) / 2);
        mCameraPreviewXCentre = (int)(mCameraOverlayView.getWidth() * 0.5);
        mCameraPreviewYCentre = (int)(mCameraOverlayView.getHeight() * 0.5);
        mResizedCameraPreviewWidth = (int)(previewSize.getHeight() * 0.75);
    }

    // draws braille cells and corresponding translation on overlay using established matrices
    public void drawBrailleTranslation(BrailleCellFinder.BrailleCellFinderOutput_t brailleCellLocations, String[][] translatedBraille, double rotation, Point rotationCentre) {

        float[] srcRectangle = new float[4];
        float[] dstRectangle = new float[4];
        float[] dstScaleRectangle = new float[4];

        clearOverlay();

        int brailleLine = 0;
        int brailleCell = 0;

        for (int i = 0; i < brailleCellLocations.brailleLines.length; i += 2) {
            srcRectangle[2] = mResizedCameraPreviewWidth - brailleCellLocations.brailleLines[i]; // y1 (0.75 * 1080
            srcRectangle[0] = mResizedCameraPreviewWidth - brailleCellLocations.brailleLines[i + 1]; // y2
            for (int j = 0; j < brailleCellLocations.brailleCellColumns.length; j += 2) {
                srcRectangle[1] = brailleCellLocations.brailleCellColumns[j]; // x1
                srcRectangle[3] = brailleCellLocations.brailleCellColumns[j + 1]; // x2
                mMatrixScale.mapPoints(dstScaleRectangle, srcRectangle);
                mMatrix.mapPoints(dstRectangle, dstScaleRectangle);
                mCameraOverlayCanvas.save();
                if (rotation != 0 || rotationCentre != null) {
                    mCameraOverlayCanvas.rotate((float) rotation, (float) mCameraPreviewXCentre, (float) mCameraPreviewYCentre);
                }
                mCameraOverlayCanvas.drawRect(dstRectangle[0] - 5, dstRectangle[1] - 5, dstRectangle[2] + 5, dstRectangle[3] + 5, mPaintBox);
                mCameraOverlayCanvas.rotate(90, dstRectangle[0] + 20, dstRectangle[3] - 70);
                if (translatedBraille[brailleLine][brailleCell] != null) {
                    mCameraOverlayCanvas.drawText(translatedBraille[brailleLine][brailleCell], dstRectangle[0] + 20, dstRectangle[3] - 70, mPaintText);
                }
                mCameraOverlayCanvas.restore();
                brailleCell++;
            }
            brailleLine++;
            brailleCell = 0;
        }

        mCameraOverlayView.setImageBitmap(mCameraOverlayBitmap);
        mIsOverlayActive = true;

    }

    // clears overlay
    public void clearOverlay() {

        if(mIsOverlayActive) {
            mCameraOverlayCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        mIsOverlayActive = false;

        drawCrosshair();


    }

    // draws alignment crosshair
    private void drawCrosshair() {

        mCameraOverlayCanvas.drawLine(100, mCameraOverlayBitmap.getHeight() / 2, mCameraOverlayBitmap.getWidth() - 100, mCameraOverlayBitmap.getHeight() / 2, mPaintCrosshair);
        mCameraOverlayCanvas.drawLine(mCameraOverlayBitmap.getWidth() / 2, 100, mCameraOverlayBitmap.getWidth() / 2, mCameraOverlayBitmap.getHeight() - 100, mPaintCrosshair);
        mCameraOverlayCanvas.drawCircle(mCameraOverlayBitmap.getWidth() / 2, mCameraOverlayBitmap.getHeight() / 2, 5, mPaintCrosshair);
        mCameraOverlayView.setImageBitmap(mCameraOverlayBitmap);

    }


}