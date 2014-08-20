package glass.stats;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class StatsService extends Service
{
  // START:onstart
  public final static String TAG = StatsService.class.getName();

  // END:onstart
  private StatsReceiver receiver;
  private LiveCard      liveCard;
  private RemoteViews   rv;

  @Override
  // START:onstart
  public int onStartCommand(Intent intent, int flags, int startId) {
    if( liveCard == null ) {
      // END:onstart
      Log.d( TAG, "Creating LiveCard" );
      // START:onstart
      liveCard = new LiveCard( this, TAG );
      liveCard.setViews( remoteViews() );
      liveCard.setAction( buildAction() );
      liveCard.publish( PublishMode.REVEAL );
      // END:onstart
      buildReceiver( remoteViews() );
      // START:onstart
    }
    return START_STICKY;
  }
  // END:onstart
  
  @Override
  // START:destroy
  public void onDestroy() {
    if( liveCard != null && liveCard.isPublished() ) {
      liveCard.unpublish();
      liveCard = null;
      // END:destroy
      unregisterReceiver(receiver);
      // START:destroy
    }
    super.onDestroy();
  }
  // END:destroy

  // START:buildviews
  private RemoteViews remoteViews() {
    // END:buildviews
    if( this.rv == null ) {
    // START:buildviews
    rv = new RemoteViews(getPackageName(), R.layout.stats);
    rv.setTextViewText(R.id.time, StatsUtil.getCurrentTime(this));
    rv.setTextViewText(R.id.connected, StatsUtil.getConnectedString(this));
    Configuration config = getResources().getConfiguration();
    rv.setTextViewText(R.id.language, config.locale.getDisplayLanguage());
    rv.setTextViewText(R.id.country, config.locale.getDisplayCountry());
    // END:buildviews
    }
    // START:buildviews
    return rv;
  }
  // END:buildviews

  // START:buildactions
  private PendingIntent buildAction() {
    Intent menuIntent = new Intent(this, MenuActivity.class);
    menuIntent.addFlags(
          Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    return PendingIntent.getActivity(this, 0, menuIntent, 0);
  }
  // END:buildactions

  // START:receiver
  private void buildReceiver(RemoteViews rv) {
    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_POWER_CONNECTED);
    filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
    filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
    filter.addAction(Intent.ACTION_BATTERY_CHANGED);
    filter.addAction(Intent.ACTION_TIME_TICK); // update every minute
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    receiver = new StatsReceiver(liveCard, rv);
    registerReceiver(receiver, filter);
  }
  // END:receiver

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
