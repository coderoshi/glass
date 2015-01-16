package glass.partify;

import static glass.partify.Log.d;

import java.io.IOException;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class PartyService extends Service {

    public final static String TAG          = PartyService.class.getName();

    private LiveCard           liveCard;
    private LiveCardRenderer   renderer;
    private final GifferBinder binder       = new GifferBinder();
    private int                balloonCount = LiveCardRenderer.DEFAULT_BALLOON_COUNT;

    // Binder giving access to this service.
    // START:binder
    public class GifferBinder extends Binder {
        public PartyService getService() {
            return PartyService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    // END:binder

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        d("onStartCommand");
        if (liveCard == null) {
            liveCard = new LiveCard(this, TAG);
            // START:directrender
            renderer = new LiveCardRenderer(this);
            liveCard.setDirectRenderingEnabled(true);
            liveCard.getSurfaceHolder().addCallback(renderer);
            // END:directrender
            liveCard.setAction(buildAction());
            liveCard.publish(PublishMode.REVEAL);
        } else {
            liveCard.navigate();
        }
        return START_STICKY;
    }

    private PendingIntent buildAction() {
        Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
        menuIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, menuIntent, 0);
    }

    @Override
    public void onDestroy() {
        d("onDestroy");
        if( liveCard != null && liveCard.isPublished() ) {
            liveCard.getSurfaceHolder().removeCallback(renderer);
            liveCard.unpublish();
            // in case this isn't called by Android, kill the background thread
            renderer.surfaceDestroyed(liveCard.getSurfaceHolder());
            liveCard = null;
            renderer = null;
        }
        super.onDestroy();
    }

    // START:camera
    public void setImageFileName(String fileName) {
        // END:camera
        d("setImageFileName " + fileName);
        // START:camera
        if( renderer != null ) {
            Bitmap background = BitmapFactory.decodeFile( fileName );
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int width = dm.widthPixels;   // 640px
            int height = dm.heightPixels; // 360px
            background = Bitmap.createScaledBitmap( background,width,height,true );
            renderer.setBackgroundImage( background );
        }
    }
    // END:camera

    // START:setBalloonCount
    public void setBalloonCount(int balloonCount) {
        // END:setBalloonCount
        d("setBalloonCount " + balloonCount);
        // START:setBalloonCount
        this.balloonCount = balloonCount;
        if (renderer != null) {
            // END:setBalloonCount
            try {
                // START:setBalloonCount
                renderer.loadBalloons(balloonCount);
                // END:setBalloonCount
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // START:setBalloonCount
        }
    }
    public int getBalloonCount() {
        return balloonCount;
    }
    // END:setBalloonCount
}
