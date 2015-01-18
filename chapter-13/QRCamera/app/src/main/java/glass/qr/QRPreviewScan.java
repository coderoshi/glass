package glass.qr;

import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

// START:preview
public class QRPreviewScan implements Camera.PreviewCallback {
// END:preview
  public final static String TAG = QRPreviewScan.class.getName();
  private static final int   SCANS_PER_SEC = 3;
  private int                framesSinceLastScan;
  private int                widthPixels;
  private int                heightPixels;
  private MultiFormatReader  multiFormatReader;
  private QRCameraActivity   activity;

  public QRPreviewScan(QRCameraActivity activity, int widthPixels, int heightPixels) {
    this.framesSinceLastScan = 0;
    this.activity = activity;
    this.widthPixels = widthPixels;
    this.heightPixels = heightPixels;
    this.multiFormatReader = new MultiFormatReader();
  }

  // START:preview
  public void onPreviewFrame(byte[] data, Camera camera) {
    // Only scan every 10th frame
    if( ++framesSinceLastScan % (QRCameraView.FPS / SCANS_PER_SEC) == 0 ) {
      scan(data, widthPixels, heightPixels);
      framesSinceLastScan = 0;
    }
  }
  //END:preview

  /*
  // START:async
  private static AtomicBoolean scanning = new AtomicBoolean(false);
  class ScanTask extends AsyncTask<PlanarYUVLuminanceSource, Result, Result> {
    @Override
    protected Result doInBackground( PlanarYUVLuminanceSource... params ) {
      if( scanning.getAndSet(true) ) return null;
      PlanarYUVLuminanceSource luminanceSource = params[0];
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
      try {
        return multiFormatReader.decodeWithState(bitmap);
      } catch (ReaderException re) { // nothing found to decode
      } finally {
        multiFormatReader.reset();
      }
      scanning.set(false);
      return null;
    }
    @Override
    protected void onPostExecute(Result result) {
      if( result != null ) {
        Intent intent = new QRIntentBuilder(result.getText()).buildIntent();
        activity.launchIntent(intent);
        scanning.set(false);
      }
    }
  }
  // END:async
  //*/

  // START:scan
  private void scan(byte[] data, int width, int height) {
    // END:scan
    Log.d(TAG, "scan");
    // START:scan
    PlanarYUVLuminanceSource luminanceSource = new PlanarYUVLuminanceSource(data,
        width, height, 0, 0, width, height, false);
    // END:scan
    // new ScanTask().execute(luminanceSource); // uncomment to use ScanTask
    // START:scan
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
    Result result = null;
    try {
      result = multiFormatReader.decodeWithState(bitmap);
    } catch (ReaderException re) { // nothing found to decode
    } finally {
      multiFormatReader.reset();
    }
    if (result != null) {
      Intent intent = new QRIntentBuilder(result.getText()).buildIntent();
      activity.launchIntent(intent);
    }
  }
  // END:scan
}
