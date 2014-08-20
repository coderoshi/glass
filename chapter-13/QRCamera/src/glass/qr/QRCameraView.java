package glass.qr;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.glass.timeline.DirectRenderingCallback;

/**
 * @author eric redmond
 * @twitter coderoshi
 */
@SuppressLint("ViewConstructor")
// START:cameraView
public class QRCameraView
    extends SurfaceView
    implements DirectRenderingCallback
{
  public final static String TAG = QRCameraView.class.getName();
  public static final int   FPS = 30;

  private Camera camera;

  public QRCameraView( QRCameraActivity context ) {
    super(context);
    getHolder().addCallback(this);
  }

  // END:cameraView
  
  // START:renderCallbacks
  public void surfaceCreated(SurfaceHolder holder) {}

  public void surfaceChanged(SurfaceHolder holder, int format,
                             int width, int height) {
    // END:renderCallbacks
    Log.d(TAG, "surfaceChanged");
    // START:renderCallbacks
    this.camera = openCamera(holder);
    // END:renderCallbacks
    Log.d(TAG, "setPreviewDisplay:" + holder.getSurface());
    // START:renderCallbacks
    this.camera.startPreview();
  }
  
  public void surfaceDestroyed(SurfaceHolder holder) {
    // END:renderCallbacks
    Log.d(TAG, "surfaceDestroyed");
    // START:renderCallbacks
    releaseCamera();
  }
  
  public void renderingPaused(SurfaceHolder holder, boolean paused) {
    if (paused) {
      // END:renderCallbacks
      Log.d(TAG, "renderingPaused PAUSED");
      // START:renderCallbacks
      releaseCamera();
    } else {
      // END:renderCallbacks
      Log.d(TAG, "renderingPaused UNPAUSED");
      // START:renderCallbacks
      if (holder.getSurface().isValid()) {
        this.camera = openCamera(holder);
        this.camera.startPreview();
      }
    }
  }
  // END:renderCallbacks
  
  // START:openCamera
  private Camera openCamera(SurfaceHolder holder) {
    if (this.camera != null)
      return this.camera;
    Camera camera = Camera.open();
    try {
      // Glass camera patch
      Parameters params = camera.getParameters();
      params.setPreviewFpsRange(FPS * 1000, FPS * 1000);
      final DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
      params.setPreviewSize(dm.widthPixels, dm.heightPixels); // 640, 360
      // params.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
      camera.setParameters(params);
      camera.setPreviewDisplay(holder);
      QRCameraActivity activity = (QRCameraActivity)getContext();
      camera.setPreviewCallback(
        new QRPreviewScan(activity, dm.widthPixels, dm.heightPixels));
    } catch (IOException e) {
      camera.release();
      camera = null;
      Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
      e.printStackTrace();
    }
    return camera;
  }
  // END:openCamera

  // START:releaseCamera
  private synchronized void releaseCamera() {
    if (this.camera != null) {
      try {
        this.camera.setPreviewDisplay(null);
      } catch (IOException e) {
      }
      this.camera.setPreviewCallback(null);
      this.camera.stopPreview();
      this.camera.release();
      this.camera = null;
    }
  }
  // END:releaseCamera
}
