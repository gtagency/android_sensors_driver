package org.ros.android.android_sensors_driver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.ros.android.android_sensors_driver.CameraStreamer.StreamCallback;
import org.ros.internal.message.MessageBuffers;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.namespace.NameResolver;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import sensor_msgs.CameraInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

public class CameraPublisher extends AbstractNodeMain {

	private Publisher<sensor_msgs.Image> imagePublisher;
	private ConnectedNode connectedNode;
	private Publisher<sensor_msgs.CameraInfo> cameraInfoPublisher;
	private ChannelBufferOutputStream stream;
	private CameraStreamer mCameraStreamer;

	public CameraPublisher(CameraStreamer mCameraStreamer) {
		this.mCameraStreamer = mCameraStreamer;
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("android_camera");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		NameResolver resolver = null;
		resolver = NameResolver.newFromNamespace(connectedNode.getName());

		this.imagePublisher = connectedNode.newPublisher(
				resolver.resolve("image_raw"),
				sensor_msgs.Image._TYPE);
		this.cameraInfoPublisher = connectedNode.newPublisher(
				resolver.resolve("camera_info"), sensor_msgs.CameraInfo._TYPE);
		// this.rawImagePublisher =
		// connectedNode.newPublisher(resolver.resolve("image/raw"),
		// sensor_msgs.Image._TYPE);
		this.stream = new ChannelBufferOutputStream(
				MessageBuffers.dynamicBuffer());
		this.connectedNode = connectedNode;
		
		mCameraStreamer.setStreamCallback(new StreamCallback() {

			@Override
			public void onFrameReceived(byte[] data) {
				publishFrame(data,
							 mCameraStreamer.getFrameWidth(),
							 mCameraStreamer.getFrameHeight());
			}
		});
	}

	
	public void publishFrame(byte[] data, int width, int height) {
		
		Time currentTime = connectedNode.getCurrentTime();

		sensor_msgs.Image image = imagePublisher.newMessage();
		image.setEncoding("nv21");
		image.setWidth(width);
		image.setHeight(height);
		//TODO: is this right?
		image.setStep(data.length / height);
		image.getHeader().setStamp(currentTime);
		image.getHeader().setFrameId("camera");
		// measureTime[2] = connectedNode.getCurrentTime();

		CameraInfo cameraInfo = cameraInfoPublisher.newMessage();
		cameraInfo.getHeader().setFrameId("camera");
		cameraInfo.getHeader().setStamp(currentTime);
		cameraInfo.setWidth(width);
		cameraInfo.setHeight(height);
		cameraInfoPublisher.publish(cameraInfo);

//        Size previewSize = mCamera.getParameters().getPreviewSize(); 
//        YuvImage yuvimage=new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        yuvimage.compressToJpeg(new Rect(0, 0, width/4, height/4), 50, baos);

//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

		stream.buffer().writeBytes(data);

		image.setData(stream.buffer().copy());

		stream.buffer().clear();
		imagePublisher.publish(image);

	}

	// public void updateLocation(Location location) {
	// //TODO: fill out rest of nav information, and also publish velocity and
	// time_reference
	// // sensor_msgs.NavSatStatus.
	// sensor_msgs.NavSatFix msg = fixPublisher.newMessage();
	// //time since jan 1 1970...convert msec to seconds (double)
	// msg.getHeader().setStamp(new Time(location.getTime()/1000.0));
	// //NOTE: this may not be right...android reports alt above sealevel, ROS
	// expects altitude above ellipsoid
	// msg.setAltitude(location.getAltitude());
	// msg.setLatitude(location.getLatitude());
	// msg.setLongitude(location.getLongitude());
	// // sensor_msgs.NavSatStatus status;
	// // status.
	// //TODO: accuracy, velocity (Twist)
	// // TODO Auto-generated method stub
	// fixPublisher.publish(msg);
	// }

	protected void startRecording() throws IOException {
//		if (mCamera == null)
//			mCamera = Camera.open();
//
//		mCamera.lock();
//		mCamera.setPreviewCallback(new PreviewCallback() {
//
//			@Override
//			public void onPreviewFrame(byte[] data, Camera camera) {
//				Log.i(CameraPublisher.class.getCanonicalName(), "" + data.length);
//				publishFrame(data);
//			}
//		});
//		mCamera.startPreview();
	}

//	protected void stopRecording() {
//
//		if (mrec != null) {
//			mrec.stop();
//			mrec.release();
//			mCamera.release();
//			mCamera.lock();
//		}
//	}
//
//	private void releaseMediaRecorder() {
//
//		if (mrec != null) {
//			mrec.reset(); // clear recorder configuration
//			mrec.release(); // release the recorder object
//		}
//	}
//
//	private void releaseCamera() {
//		if (mCamera != null) {
//			mCamera.release(); // release the camera for other applications
//			mCamera = null;
//		}
//
//	}
}
