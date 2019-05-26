package org.example.brailleassistant.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Camera {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    // Camera State Machine
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_STILL_CAPTURE_REQUESTED = 1;
    private int mCameraState;

    // Camera Torch Status
    private Boolean mCameraTorchStatus = false;

    // Camera Preview Texture
    private TextureView mCameraView;

    // Image
    private ImageReader mImageReader;

    // Camera related members
    private String mCameraId;
    private Size mPreviewSize;
    private CameraDevice mCameraDevice;

    // Capture Requests / Sessions
    private CaptureRequest mPreviewCaptureRequest;
    private CaptureRequest.Builder mPreviewCaptureRequestBuilder;
    private CameraCaptureSession mCameraCaptureSession;

    // Zoom related member variables
    private float mFingerSpacing = 0;
    private float mZoomLevel = 1.0f;
    private float mMaximumZoomLevel;
    private Rect mCurrentZoom;

    // Camera 2 Thread
    private HandlerThread mCamera2Thread;
    private Handler mCamera2Handler;

    // Image Processing Thread
    private HandlerThread mImageProcThread;
    private Handler mImageProcHandler;

    // Image Saved Callback
    private CameraFrameReader.OnFrameEventListener mOnFrameEventListener;

    // Preview Size callback
    private OnCameraPreviewSizeChangedListener mOnCameraPreviewSizeChangedListener;

    // Activity Context
    private Context mActivityContext;

    // Callback interface to tell other objects the preview size has changed
    public interface OnCameraPreviewSizeChangedListener {
        void OnPreviewSizeChanged(Size previewSize);
    }

    // Constructor
    public Camera(Context activityContext, TextureView cameraPreviewTexture, CameraFrameReader.OnFrameEventListener onFrameEventListener, OnCameraPreviewSizeChangedListener onCameraPreviewSizeChangedListener) {
        this.mCameraView = cameraPreviewTexture;
        this.mCameraView.setOnTouchListener(onTouchListener);
        this.mOnFrameEventListener = onFrameEventListener;
        this.mOnCameraPreviewSizeChangedListener = onCameraPreviewSizeChangedListener;
        this.mActivityContext = activityContext;
    }

    public void getCameraFrame() {

        captureStillImage();

    }

    public void toggleCameraTorch() {

        mCameraTorchStatus = !mCameraTorchStatus;

        int flashMode = (mCameraTorchStatus) ? CaptureRequest.FLASH_MODE_TORCH : CaptureRequest.FLASH_MODE_OFF;

        try {
            mPreviewCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, flashMode);
            mCameraCaptureSession.setRepeatingRequest(mPreviewCaptureRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void onResumeHandler() {
        openBackgroundThread();
        if (mCameraView.isAvailable()) {
            setupCamera(mCameraView.getWidth(), mCameraView.getHeight());
            openCamera();
        } else {
            mCameraView.setSurfaceTextureListener(textureListener);
        }
    }

    public void onPauseHandler() {
        closeCamera();
        closeBackgroundThread();
    }

    // Setup listener for camera surface texture
    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mImageProcHandler.post(new CameraFrameReader(reader.acquireLatestImage(), mOnFrameEventListener));
        }
    };

    // Setup callback for a connected camera device
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    // Setup callback for camera capture sessions
    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void processCapture(CaptureResult result) {
            switch(mCameraState) {
                case STATE_PREVIEW:
                    // Do nothing
                    break;
                case STATE_STILL_CAPTURE_REQUESTED:
                    // Do nothing
                    break;
            }
        }

        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            processCapture(result);
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

    };

    // Detects zoom gesture for camera preview
    // https://stackoverflow.com/questions/32711975/zoom-camera2-preview-using-textureview
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            CameraManager cameraManager = (CameraManager) mActivityContext.getSystemService(Context.CAMERA_SERVICE);
            try {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(mCameraId);
                Rect rect = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                if (rect == null) return false;
                float currentFingerSpacing;

                if (event.getPointerCount() == 2) { //Multi touch.
                    currentFingerSpacing = getFingerSpacing(event);
                    float delta = 0.05f; //Control this value to control the zooming sensibility
                    if (mFingerSpacing != 0) {
                        if (currentFingerSpacing > mFingerSpacing) { //Don't over zoom-in
                            if ((mMaximumZoomLevel - mZoomLevel) <= delta) {
                                delta = mMaximumZoomLevel - mZoomLevel;
                            }
                            mZoomLevel = mZoomLevel + delta;
                        } else if (currentFingerSpacing < mFingerSpacing) { //Don't over zoom-out
                            if ((mZoomLevel - delta) < 1f) {
                                delta = mZoomLevel - 1f;
                            }
                            mZoomLevel = mZoomLevel - delta;
                        }
                        float ratio = (float) 1 / mZoomLevel; //This ratio is the ratio of cropped Rect to Camera's original(Maximum) Rect
                        //croppedWidth and croppedHeight are the pixels cropped away, not pixels after cropped
                        int croppedWidth = rect.width() - Math.round((float) rect.width() * ratio);
                        int croppedHeight = rect.height() - Math.round((float) rect.height() * ratio);
                        //Finally, zoom represents the zoomed visible area
                        mCurrentZoom = new Rect(croppedWidth / 2, croppedHeight / 2,
                                rect.width() - croppedWidth / 2, rect.height() - croppedHeight / 2);
                        mPreviewCaptureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, mCurrentZoom);
                    }
                    mFingerSpacing = currentFingerSpacing;
                } else { //Single touch point, needs to return true in order to detect one more touch point
                    return true;
                }
                mCameraCaptureSession.setRepeatingRequest(mPreviewCaptureRequestBuilder.build(), null, null);
                return true;
            } catch (final Exception e) {
                //Error handling up to you
                return true;
            }

        }
    };


    // Requests camera permissions
    // Gets ID of rear camera
    // Gets optimum size for camera preview
    private void setupCamera(int width, int height) {
        CameraManager cameraManager= (CameraManager) mActivityContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            for(String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    mMaximumZoomLevel = cameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                    StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    mPreviewSize = getPreferredPreviewSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                    mOnCameraPreviewSizeChangedListener.OnPreviewSizeChanged(mPreviewSize);
                    mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.YUV_420_888, 1);
                    mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mImageProcHandler);
                    mCameraId = cameraId;
                    return;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // Gets optimum size for camera preview
    private Size getPreferredPreviewSize(Size[] map_sizes, int width, int height) {
        List<Size> collectorSizes = new ArrayList<>();
        for(Size option : map_sizes) {
            if(width > height) {
                if(option.getWidth() > width && option.getHeight() > height) {
                    collectorSizes.add(option);
                }
            } else {
                if(option.getWidth() > height && option.getHeight() > width) {
                    collectorSizes.add(option);
                }
            }
        }
        if(collectorSizes.size() > 0) {
            return Collections.min(collectorSizes, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return map_sizes[0];
    }

    // opens rear camera device
    private void openCamera() {
        CameraManager cameraManager = (CameraManager) mActivityContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(mActivityContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mCamera2Handler);
            }
        } catch(CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // closes open camera
    private void closeCamera() {
        if(mCameraTorchStatus) {
            toggleCameraTorch();
        }
        if(mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if(mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if(mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    // Initialises and begins camera preview
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = mCameraView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            mPreviewCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    if(mCameraDevice == null) {
                        return;
                    }
                    try {
                        mPreviewCaptureRequest = mPreviewCaptureRequestBuilder.build();
                        mCameraCaptureSession = session;
                        mCameraCaptureSession.setRepeatingRequest(mPreviewCaptureRequest, mSessionCaptureCallback, mCamera2Handler);
                    } catch(CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        } catch(CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // Background thread for servicing Camera2 API related callbacks and requests
    private void openBackgroundThread() {
        mCamera2Thread = new HandlerThread("Camera2 Background Thread");
        mCamera2Thread.start();
        mCamera2Handler = new Handler(mCamera2Thread.getLooper());
        mImageProcThread = new HandlerThread("Image Processsing Thread");
        mImageProcThread.start();
        mImageProcHandler = new Handler(mImageProcThread.getLooper());
    }

    // Closes background thread safely
    private void closeBackgroundThread() {
        mCamera2Thread.quitSafely();
        try {
            mCamera2Thread.join();
            mCamera2Thread = null;
            mCamera2Handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mImageProcThread.quitSafely();
        try {
            mImageProcThread.join();
            mImageProcThread = null;
            mImageProcHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void captureStillImage() {
        try {
            mCameraState = STATE_STILL_CAPTURE_REQUESTED;
            CaptureRequest.Builder captureStillBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureStillBuilder.addTarget(mImageReader.getSurface());
            SurfaceTexture surfaceTexture = mCameraView.getSurfaceTexture();
            Surface previewSurface = new Surface(surfaceTexture);
            captureStillBuilder.addTarget(previewSurface);

            int rotation = ((Activity) mActivityContext).getWindowManager().getDefaultDisplay().getRotation();
            captureStillBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            int flashMode = (mCameraTorchStatus) ? CaptureRequest.FLASH_MODE_TORCH : CaptureRequest.FLASH_MODE_OFF;
            captureStillBuilder.set(CaptureRequest.FLASH_MODE, flashMode);
            int hotPixelMode = (mCameraTorchStatus) ? CaptureRequest.HOT_PIXEL_MODE_HIGH_QUALITY : CaptureRequest.HOT_PIXEL_MODE_OFF;
            captureStillBuilder.set(CaptureRequest.HOT_PIXEL_MODE, hotPixelMode);
            captureStillBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, CaptureRequest.CONTROL_SCENE_MODE_STEADYPHOTO);
            // Apply zoom
            if (mCurrentZoom != null) {
                captureStillBuilder.set(CaptureRequest.SCALER_CROP_REGION, mCurrentZoom);
            }
            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    mCameraState = STATE_PREVIEW;
                }
            };

            // Running in background thread already - null
            mCameraCaptureSession.capture(captureStillBuilder.build(), captureCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // https://stackoverflow.com/questions/32711975/zoom-camera2-preview-using-textureview
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
