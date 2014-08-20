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

public class PartyService extends Service
{
  public final static String TAG          = PartyService.class.getName();

  private LiveCard           liveCard;
  private PartyDrawer        drawer;
  private final GifferBinder binder       = new GifferBinder();
  private int                balloonCount = PartyDrawer.DEFAULT_BALLOON_COUNT;

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
      drawer = new PartyDrawer(this);
      liveCard.setDirectRenderingEnabled(true);
      liveCard.getSurfaceHolder().addCallback(drawer);
      // END:directrender
      liveCard.setAction(buildAction());
      liveCard.publish(PublishMode.REVEAL);
    }
    return START_STICKY;
  }
  
  private PendingIntent buildAction() {
    Intent menuIntent = new Intent(this, MenuActivity.class);
    menuIntent.addFlags(
          Intent.FLAG_ACTIVITY_NEW_TASK
        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    return PendingIntent.getActivity(this, 0, menuIntent, 0);
  }
  
  @Override
  public void onDestroy() {
    d("onDestroy");
    if( liveCard != null && liveCard.isPublished() ) {
      liveCard.getSurfaceHolder().removeCallback(drawer);
      liveCard.unpublish();
      // in case this isn't called by Android, kill the background thread
      drawer.surfaceDestroyed(liveCard.getSurfaceHolder());
      liveCard = null;
      drawer = null;
    }
    super.onDestroy();
  }
  
  // START:camera
  public void setImageFileName(String fileName) {
    // END:camera
    d("setImageFileName " + fileName);
    // START:camera
    if( drawer != null ) {
      Bitmap background = BitmapFactory.decodeFile( fileName );
      DisplayMetrics dm = getResources().getDisplayMetrics();
      int width = dm.widthPixels;   // 640px
      int height = dm.heightPixels; // 360px
      background = Bitmap.createScaledBitmap( background,width,height,true );
      drawer.setBackgroundImage( background );
    }
  }
  // END:camera
  
  // START:setBalloonCount
  public void setBalloonCount(int balloonCount) {
    // END:setBalloonCount
    d("setBalloonCount " + balloonCount);
    // START:setBalloonCount
    this.balloonCount = balloonCount;
    if (drawer != null) {
      // END:setBalloonCount
      try {
      // START:setBalloonCount
      drawer.loadBalloons(balloonCount);
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
