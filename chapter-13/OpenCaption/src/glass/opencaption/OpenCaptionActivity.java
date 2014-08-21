package glass.opencaption;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.glass.app.Card;

/**
 * @author Eric Redmond
 * @twitter coderoshi
 */
// START:locationChanged
public class OpenCaptionActivity
  extends Activity
  implements LocationListener
{
  // ...other code...
//END:locationChanged
  private static final String TAG = OpenCaptionActivity.class.getName();

  private LocationManager locationManager;
  private String locationMessage = null;

  @Override
  // START:startSpeechPrompt
  // START:locations
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // END:startSpeechPrompt
    setupLocationManager();
    // START:startSpeechPrompt
    startSpeechPrompt( 1 );
  }

  // END:locations
  private void startSpeechPrompt( int speechRequest ) {
    Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
    intent.putExtra( RecognizerIntent.EXTRA_PROMPT, R.string.voice_prompt );
    startActivityForResult( intent, speechRequest );
  }
  // END:startSpeechPrompt

  /*
  // START:prompt
  protected void onResume() {
    // END:prompt
    Log.d(TAG, "onResume");
    // START:prompt
    super.onResume();
    List<String> results =
        getIntent().getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
    if( results.isEmpty() ) {
      String spokenText = results.get(0);
      // Create a card with spokenText
      Card card = new Card( this )
        .setText( spokenText );
      card.getView();

      // display the card. when tapped it goes away.
      // http://stackoverflow.com/questions/8701634/send-email-intent
      // http://stackoverflow.com/questions/22004590/gdk-send-email-with-attachment
    }
    finish();
  }
  // END:prompt
  */

  @Override
  // START:results
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent intent) {
    // END:results
    Log.d(TAG, "onActivityResult resultCode: " + resultCode);
    // START:results
    if( resultCode == RESULT_OK ) {
      List<String> results =
          intent.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
      if( !results.isEmpty() ) {
        String spokenText = results.get(0);

        // TODO: save data to disk, or send to google drive!?
        // Create a card with spokenText
        Card card = new Card( this )
            .setText( spokenText );
        // END:results
        card.setFootnote( getLocationMessage() );
        // START:results
        card.getView();
      }

      // Close the speech prompt
      stopService( intent );

      // run recognizer again, increment the next request code
      startSpeechPrompt( ++requestCode );
    } else if( resultCode == RESULT_CANCELED ) {
      // end this activity
      finish();
    }
    super.onActivityResult(requestCode, resultCode, intent);
  }
  // END:results

  private synchronized void setLocationMessage(String locMsg) {
    this.locationMessage = locMsg;
  }
  
  private String getLocationMessage() {
    if( this.locationMessage != null ) {
      return this.locationMessage;
    } else {
      return "unknown location";
    }
  }

  // START:locations
  private void setupLocationManager() {
    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    Criteria criteria = new Criteria();
    criteria.setAccuracy( Criteria.ACCURACY_COARSE );
    // criteria.setAltitudeRequired(true);

    List<String> providers = locationManager.getProviders(criteria, true);
    for (String provider : providers) {
      locationManager.requestLocationUpdates(provider, 15*1000, 100, this);
    }
  }
  // END:locations

  @Override
  public void onStatusChanged(String provider, int stat, Bundle extras) { }

  @Override
  public void onProviderEnabled(String provider) { }

  @Override
  public void onProviderDisabled(String provider) { }

  @Override
  // START:locationChanged
  public void onLocationChanged(Location location) {
    if( this.locationMessage == null ) {
      Toast.makeText(this, R.string.location_found, Toast.LENGTH_LONG).show();
    }
    String msg = 
        location.getLatitude() + "," +
        location.getLongitude();
    setLocationMessage( msg );
  }
}
//END:locationChanged
