package org.ros.android.android_sensors_driver;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * This class is the closest thing to providing a stream interface.
 * I can find.  It uses the preview interface on the camera to 
 *
 */
public class CameraStreamer extends SurfaceView implements
        SurfaceHolder.Callback {
    
    public interface StreamCallback {
        public void onFrameReceived(byte[] data);
    };

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private StreamCallback streamCallback = null;
	private Size frameSize;

    private class MyPreviewCallback implements PreviewCallback {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (camera == mCamera && streamCallback != null) {
            	long timeMS = System.currentTimeMillis();
                streamCallback.onFrameReceived(data);
                long elapsed = System.currentTimeMillis() - timeMS;
                Log.i(TAG, String.format("%d milliseconds elapsed", elapsed));
            }
        }
    }

    private static String TAG = CameraStreamer.class.getCanonicalName();
    public CameraStreamer(Activity activity, Camera camera, ViewGroup group) {
        super(activity);
        mCamera = camera;
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        FrameLayout fl = new FrameLayout(activity);
        fl.addView(this);
        activity.addContentView(fl, new FrameLayout.LayoutParams(1, 1));
        mCamera.getParameters().setPreviewSize(320, 240);
//        fl.setVisibility(View.INVISIBLE);
        this.frameSize = mCamera.getParameters().getPreviewSize();
    }

    public int getFrameWidth() {
		return this.frameSize.width;
	}
    
    public int getFrameHeight() {
    	return this.frameSize.height;
	}

    public void setStreamCallback(StreamCallback streamCallback) {
        this.streamCallback = streamCallback;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(new MyPreviewCallback());
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	String pp = "hello there";
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(new MyPreviewCallback());
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}