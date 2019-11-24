package org.example.brailleassistant.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.example.brailleassistant.R;
import org.example.brailleassistant.utils.BrailleCellCalculator;
import org.example.brailleassistant.utils.BrailleCellFinder;
import org.example.brailleassistant.utils.BrailleCellParser;
import org.example.brailleassistant.utils.BrailleFilter;
import org.example.brailleassistant.utils.BrailleRotationCorrection;
import org.example.brailleassistant.utils.Camera;
import org.example.brailleassistant.utils.CameraFrameReader;
import org.example.brailleassistant.utils.CameraOverlayRenderer;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java4");
    }

    // Permission constants
    private static final int CAMERA_REQUEST_PERMISSION = 1;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 2;

    // Variable to switch recognition on and off
    private volatile Boolean mBrailleCellRecognitionActive = false;
    private volatile Boolean mBrailleRotationCorrectionActive = false;

    // UI Widgets
    private TextureView mCameraView;
    private ImageView mCameraOverlayView;
    private Button mToggleCameraTorchButton;
    private Button mBrailleRecognitionButton;
    private Button mScreenCaptureButton;
    private Button mRotationCorrectionButton;

    // Camera and Overlay Renderer
    private CameraOverlayRenderer mCameraOverlayRenderer;
    private Camera mCamera;

    // Braille Recognition Modules
    private BrailleFilter mBrailleFilter;
    private BrailleRotationCorrection mBrailleRotationCorrection;
    private BrailleCellFinder mCellFinder;
    private BrailleCellCalculator mCellCalculator;
    private BrailleCellParser mCellParser;


    // Called when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup views
        mCameraView = findViewById(R.id.camera_view);
        assert mCameraView != null;
        mCameraOverlayView = findViewById(R.id.camera_overlay_view);
        assert mCameraOverlayView != null;
        mCameraOverlayView.bringToFront();
        // Setup Buttons
        mToggleCameraTorchButton = findViewById(R.id.camera_torch_button);
        assert mToggleCameraTorchButton != null;
        mBrailleRecognitionButton = findViewById(R.id.braille_recognition_button);
        assert mBrailleRecognitionButton != null;
        mScreenCaptureButton = findViewById(R.id.screen_capture_button);
        assert mScreenCaptureButton != null;
        mRotationCorrectionButton = findViewById(R.id.rotation_correction_button);
        assert mRotationCorrectionButton != null;

        // Initialise  Camera and CameraOverlayRenderer
        mCameraOverlayRenderer = new CameraOverlayRenderer(mCameraOverlayView);
        mCamera = new Camera(this, mCameraView, mOnFrameEventListener, mOnCameraPreviewSizeChangedListener);

        // Initialise braille modules
        mBrailleFilter = new BrailleFilter();
        mBrailleRotationCorrection = new BrailleRotationCorrection();
        mCellFinder = new BrailleCellFinder();
        mCellCalculator = new BrailleCellCalculator();
        mCellParser = new BrailleCellParser(0);
        mBrailleFilter.setFilterType(BrailleFilter.FilterType_t.SingleSided);

    }

    // Called when activity is created or resumed after being paused
    @Override
    protected void onResume() {
        super.onResume();
        // setup camera access and camera threads
        mCamera.onResumeHandler();
        // set listeners for buttons to detect presses
        mToggleCameraTorchButton.setOnClickListener(onClickListener);
        mBrailleRecognitionButton.setOnClickListener(onClickListener);
        mScreenCaptureButton.setOnClickListener(onClickListener);
        mRotationCorrectionButton.setOnClickListener(onClickListener);
        // rotate buttons animation when app opens
        rotateButtons(Surface.ROTATION_0, Surface.ROTATION_90);
        // check camera access permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_PERMISSION);
        }
        // check photo storage permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    // Called when activity is paused
    @Override
    protected void onPause() {
        // manage camera access and camera threads
        mCamera.onPauseHandler();
        super.onPause();
    }

    // Called once app permissions have been set
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_PERMISSION: {
                closeAppIfPermissionsAreAbsent(grantResults);
                return;
            }
            case WRITE_EXTERNAL_STORAGE_PERMISSION: {
                closeAppIfPermissionsAreAbsent(grantResults);
                return;
            }
        }
    }

    private void closeAppIfPermissionsAreAbsent(int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish(); // close app
            }
        }
    }

    // listener for buttons
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch(v.getId()) {

                case R.id.camera_torch_button:
                    mCamera.toggleCameraTorch();
                    break;

                case R.id.screen_capture_button:
                    takeScreenshot();
                    break;

                case R.id.braille_recognition_button:
                   if(mBrailleCellRecognitionActive) {
                       mBrailleCellRecognitionActive = false;
                       Toast toast = Toast.makeText(getApplicationContext(),"Recognition Off", Toast.LENGTH_SHORT);
                       toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, -40);
                       toast.show();
                   } else {
                       mBrailleCellRecognitionActive = true;
                       Toast toast = Toast.makeText(getApplicationContext(),"Recognition On", Toast.LENGTH_SHORT);
                       toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, -40);
                       toast.show();
                       mCamera.getCameraFrame();
                   }
                   break;

                case R.id.rotation_correction_button:
                    if (mBrailleRotationCorrectionActive) {
                        mBrailleRotationCorrectionActive = false;
                        Toast toast = Toast.makeText(getApplicationContext(),"Rotation Correction Off", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, -40);
                        toast.show();
                    } else {
                        mBrailleRotationCorrectionActive = true;
                        Toast toast = Toast.makeText(getApplicationContext(),"Rotation Correction On", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, -40);
                        toast.show();
                    }
                    break;

                default:
                    break;

            }

        }

    };

    // Callback to set mapping matrix once camera preview size has been determined
    private final Camera.OnCameraPreviewSizeChangedListener mOnCameraPreviewSizeChangedListener = new Camera.OnCameraPreviewSizeChangedListener() {
        @Override
        public void OnPreviewSizeChanged(android.util.Size previewSize) {

            mCameraOverlayRenderer.setMappingMatrix(previewSize);

        }
    };

    // Callback (on Image Processing thread) - Called once "Camera.getCameraFrame()" has taken photo and converted to Mat format
    private final CameraFrameReader.OnFrameEventListener mOnFrameEventListener = new CameraFrameReader.OnFrameEventListener() {
        @Override
        public void onFrameEvent(Mat brailleImage) {

            // Braille recognition is active
            if (mBrailleCellRecognitionActive) {

                final BrailleCellFinder.BrailleCellFinderOutput_t brailleCellLocations;

                // resize camera image
                Mat brailleImageResized = new Mat();
                Size newSize = new Size();
                newSize.height = brailleImage.height() * 0.75;
                newSize.width = brailleImage.width() * 0.75;
                Imgproc.resize(brailleImage, brailleImageResized, newSize);

                // filter image
                BrailleFilter.BrailleFilterOutput_t filterOutput = mBrailleFilter.extractBrailleDots(brailleImageResized);

                // rotation correction is turned on
                if (mBrailleRotationCorrectionActive) {

                    // correct image rotation
                    final BrailleRotationCorrection.BrailleRotationCorrectionOutput_t adjustedFilterOutput = mBrailleRotationCorrection.correctImageRotation(filterOutput);

                    // find braille cells
                    brailleCellLocations = mCellFinder.calculateBrailleCells(adjustedFilterOutput.filterOutput.brailleImage);

                    // cells found are of similar dimensions - valid
                    if (brailleCellLocations.validity) {

                        // calculate cell values
                        short[][] brailleCellValues = mCellCalculator.calculateBrailleCells(brailleCellLocations.brailleLines, brailleCellLocations.brailleCellColumns, adjustedFilterOutput.filterOutput.brailleDotLocations);

                        // translate braille to English
                        final String[][] parsedBraille = mCellParser.parseBraille(brailleCellValues);

                        // draw translation on overlay
                        Handler main = new Handler(Looper.getMainLooper());
                        main.post(new Runnable() {
                            @Override
                            public void run() {
                                mCameraOverlayRenderer.drawBrailleTranslation(brailleCellLocations, parsedBraille, adjustedFilterOutput.rotation, adjustedFilterOutput.rotationCentre);
                            }
                        });

                        // request next camera frame
                        mCamera.getCameraFrame();

                    } else {

                        // draw translation on overlay
                        Handler main = new Handler(Looper.getMainLooper());
                        main.post(new Runnable() {
                            @Override
                            public void run() {
                                mCameraOverlayRenderer.clearOverlay();
                            }
                        });

                        // request next camera frame
                        mCamera.getCameraFrame();

                    }

                } else { // rotation correction is turned off

                    // find braille cells
                    brailleCellLocations = mCellFinder.calculateBrailleCells(filterOutput.brailleImage);

                    // cells found are of similar dimensions - valid
                    if (brailleCellLocations.validity) {

                        // calculate cell values
                        short[][] brailleCellValues = mCellCalculator.calculateBrailleCells(brailleCellLocations.brailleLines, brailleCellLocations.brailleCellColumns, filterOutput.brailleDotLocations);

                        // translate braille to English
                        final String[][] parsedBraille = mCellParser.parseBraille(brailleCellValues);

                        // draw translation on overlay
                        Handler main = new Handler(Looper.getMainLooper());
                        main.post(new Runnable() {
                            @Override
                            public void run() {
                                mCameraOverlayRenderer.drawBrailleTranslation(brailleCellLocations, parsedBraille, 0, null);
                            }
                        });

                        // request next camera frame
                        mCamera.getCameraFrame();

                    } else {

                        // draw translation on overlay
                        Handler main = new Handler(Looper.getMainLooper());
                        main.post(new Runnable() {
                            @Override
                            public void run() {
                                mCameraOverlayRenderer.clearOverlay();
                            }
                        });

                        // request next camera frame
                        mCamera.getCameraFrame();

                    }

                }


            } else { // Braille recognition is not active

                // draw translation on overlay
                Handler main = new Handler(Looper.getMainLooper());
                main.post(new Runnable() {
                    @Override
                    public void run() {
                        mCameraOverlayRenderer.clearOverlay();
                    }
                });

            }
        }
    };

    private File createBrailleImageFile() {

        File directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"BrailleImages");
        if(!directory.exists()) {
            directory.mkdir();
        }
        Date date = new Date();
        CharSequence now = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
        return new File(directory,now + ".jpg");
    }

    private File createScreenshotFile() {

        //File directory = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"Screenshots");
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Braille Teacher");

        if(!directory.exists()) {
            directory.mkdir();
        }
        Date date = new Date();
        CharSequence now = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
        return new File(directory,now + ".jpg");
    }

    private void takeScreenshot() {

        File file = createScreenshotFile();

        try {

            // Get Camera Preview Bitmap
            while(!mCameraView.isAvailable()) {}
            Bitmap cameraPreview = mCameraView.getBitmap();
            // Get Camera Overlay Bitmap
            mCameraOverlayView.setDrawingCacheEnabled(true);
            mCameraOverlayView.buildDrawingCache(true);
            Bitmap cameraOverlay = Bitmap.createBitmap(mCameraOverlayView.getDrawingCache());
            mCameraOverlayView.setDrawingCacheEnabled(false);
            // Merge Preview and Overlay Bitmaps
            Bitmap screenshot = combineBitmaps(cameraPreview, cameraOverlay);

            // Write to local app storage
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            screenshot.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            // Add to Gallery
            addPictureToGallery(file.toString());

            // Indicate success to user
            Toast toast = Toast.makeText(getApplicationContext(),"Screenshot Captured", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_VERTICAL, 0, -40);
            toast.show();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void addPictureToGallery(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private Bitmap combineBitmaps(Bitmap firstBitmap, Bitmap secondBitmap){

        Bitmap result = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstBitmap, 0, 0, null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        return result;
    }

    private void rotateButtons(int originalOrientation, int newOrientation) {

        float originalOrientationDegrees = 0.0f, newOrientationDegrees = 0.0f;

        switch(originalOrientation) {

            case Surface.ROTATION_0:
                originalOrientationDegrees = 0.0f;
                break;
            case Surface.ROTATION_90:
                originalOrientationDegrees = 90.0f;
                break;
            case Surface.ROTATION_180:
                originalOrientationDegrees = 180.0f;
                break;
            case Surface.ROTATION_270:
                originalOrientationDegrees = -90.0f;
                break;
            default:
                break;

        }

        switch(newOrientation) {
            
            case Surface.ROTATION_0:
                newOrientationDegrees = 0.0f;
                break;
            case Surface.ROTATION_90:
                newOrientationDegrees = 90.0f;
                break;
            case Surface.ROTATION_180:
                newOrientationDegrees = 180.0f;
                break;
            case Surface.ROTATION_270:
                newOrientationDegrees = -90.0f;
                break;
            default:
                break;

        }

        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(originalOrientationDegrees, newOrientationDegrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(500);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        mToggleCameraTorchButton.startAnimation(animSet);
        mBrailleRecognitionButton.startAnimation(animSet);
        mScreenCaptureButton.startAnimation(animSet);
        mRotationCorrectionButton.startAnimation(animSet);
    }

}
