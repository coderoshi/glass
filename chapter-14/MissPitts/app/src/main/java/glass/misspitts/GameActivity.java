package glass.misspitts;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.glass.app.Card;
import com.google.android.glass.app.Card.ImageLayout;

// START:oncreate
public class GameActivity extends Activity {
  // END:oncreate
  private RenderView    renderView;
  private GameEngine    engine;
  private UserInputManager  inputManager;

  @Override
  // START:oncreate
  public void onCreate( Bundle savedInstanceState ) {
    super.onCreate( savedInstanceState );
    setContentView( loadingCardView() );
    inputManager = new UserInputManager( this );
    final Bitmap frameBuffer = Bitmap.createBitmap( 640, 360, Config.RGB_565 );
    engine = new GameEngine( this, inputManager, frameBuffer );
    renderView = new RenderView( GameActivity.this, engine, frameBuffer );
    // After 3 seconds, switch the content view to the rendered game
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        setContentView( renderView );
      }
    }, 3000);
  }
  // END:oncreate

  // START:loading
  private View loadingCardView() {
    Card loadingCard = new Card( this );
    loadingCard.setImageLayout( ImageLayout.FULL );
    loadingCard.addImage( R.drawable.loading );
    loadingCard.setText(
        getResources().getString(R.string.high_score) + getSavedScores() );
    View cardView = loadingCard.getView();
    cardView.setKeepScreenOn( true );
    return cardView;
  }
  // END:loading

  @Override
  // START:sensor
  public void onResume() {
    super.onResume();
    inputManager.registerSensor();
    renderView.setKeepScreenOn( true );
    renderView.resume();
  }
  // END:sensor

  @Override
  // START:sensor
  public void onPause() {
    super.onPause();
    inputManager.unregisterSensor();
    renderView.setKeepScreenOn( false );
    renderView.pause();
    if( isFinishing() ) {
      saveScores();
      // engine.releaseAssets();
    }
  }
  // END:sensor

  // START:scores
  private static final String PREF_SCORES = "scores";
  private static final String PREF_HIGH_SCORE = "highScore";
  private void saveScores() {
    SharedPreferences sp = getSharedPreferences(PREF_SCORES, Context.MODE_PRIVATE);
    int lastScore = engine.getScore().getValue();
    int highScore = sp.getInt( PREF_HIGH_SCORE, 0 );
    // update the shared pref value if this is a new high score
    if( lastScore > highScore ) {
      sp.edit().putInt( PREF_HIGH_SCORE, lastScore ).commit();
    }
  }
  private int getSavedScores() {
    SharedPreferences sp = getSharedPreferences(PREF_SCORES, Context.MODE_PRIVATE);
    return sp.getInt( PREF_HIGH_SCORE, 0 );
  }
  // END:scores
  
  @Override
  // START:tap
  public boolean onGenericMotionEvent( MotionEvent event ) {
    // Send generic motion events to InputManager's GestureDetector
    return inputManager.dispatchGenericFocusedEvent( event );
  }
  // END:tap
}