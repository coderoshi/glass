package glass.qr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardBuilder.Layout;

/**
 * @author eric redmond
 * @twitter coderoshi
 */
public class QRCameraActivity
        extends Activity
{
    public final static String TAG = QRCameraActivity.class.getName();

    private static final int REQUEST_CODE = 100;

    private QRCameraView       cameraView;
    private View               overlayView;
    private FrameLayout        layout;
    private AudioManager       audioManager;

    // START:overlay
    static class OverlayView extends View {
        private Paint paint;
        private String overlay;
        public OverlayView(Context context) {
            super(context);
            this.paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            this.overlay = this.getResources().getString(R.string.qr_camera_overlay);
        }
        public void draw( Canvas canvas ) {
            // draw drop shadows
            paint.setColor( Color.BLACK );
            drawBox( canvas, 1 );
            drawText( canvas, 1 );
            // draw box and text
            paint.setColor( Color.WHITE );
            drawBox( canvas, 0 );
            drawText( canvas, 0 );
        }
        private void drawBox( Canvas canvas, int offset ) {
            paint.setStrokeWidth( 6 );
            paint.setStyle( Style.STROKE );
            canvas.drawRect( 40-offset, 40-offset, 600+offset, 320+offset, paint );
        }
        private void drawText( Canvas canvas, int offset ) {
            // paint.setTypeface( Typeface.create("Roboto", 0) );
            paint.setTextSize( 32 );
            canvas.drawText( this.overlay, 90+offset, 300+offset, paint );
        }
    }
    // END:overlay

    @Override
    // START:create
    protected void onCreate(Bundle savedInstanceState) {
        // END:create
        Log.d(TAG, "onCreate");
        // START:create
        super.onCreate(savedInstanceState);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        cameraView = new QRCameraView( this );
        overlayView = new OverlayView( this );
        layout = new FrameLayout( this );
        layout.setKeepScreenOn( true );
        layout.addView( cameraView );  // order is important here
        layout.addView( overlayView ); // the last view is on top
        setContentView( layout );
    }
    // END:create

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        cameraView.renderingPaused(cameraView.getHolder(), true);
        super.onPause();
    }

    @Override
    // START:camButtonClick
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            // Release the camera by pausing the cameraView
            cameraView.renderingPaused(cameraView.getHolder(), true);
            return false;  // propgate this click onward
        }
        // END:camButtonClick
        // START:tap
        else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
            openOptionsMenu();
            return true;
        }
        // END:tap
        // START:camButtonClick
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    // END:camButtonClick

    @Override
    // START:camButtonClick
    protected void onResume() {
        // END:camButtonClick
        Log.d(TAG, "onResume");
        // START:camButtonClick
        super.onResume();
        // Re-acquire the camera and start the preview.
        cameraView.renderingPaused(cameraView.getHolder(), false);
    }
    // END:camButtonClick

    @Override
    // START:menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.qr, menu);
        return true;
    }

    // END:menu
    @Override
    // START:menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // END:menu

    // START:launchIntent
    public synchronized void launchIntent(Intent intent) {
        // END:launchIntent
        Log.d(TAG, "launchIntent");
        // START:launchIntent
        cameraView.renderingPaused(cameraView.getHolder(), true);
        if (intent.hasExtra("text")) {
            // no intent to launch, just show the text
            CardBuilder card = new CardBuilder(this, Layout.TEXT)
                    .setText(intent.getStringExtra("text"));
            layout.removeAllViews();
            layout.addView( card.getView() );
        } else {
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // END:launchIntent
        Log.d(TAG, "requestCode=" + requestCode + ",resultCode=" + resultCode);
        // START:launchIntent
        switch (requestCode) {
            case REQUEST_CODE:
                finish();
                break;
            default:
                break;
        }
    }
    // END:launchIntent
}
