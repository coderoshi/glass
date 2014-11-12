package glass.notoriety;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import app.notoriety.models.Note;
import app.notoriety.models.NoteList;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardBuilder.Layout;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

public class MainActivity
  extends Activity
  implements GestureDetector.BaseListener, LocationListener
{
  private static final int SPEECH_REQ_CODE = 101;

  private GestureDetector    mGestureDetector;
  private AudioManager       mAudioManager;
  private LocationManager    mLocationManager;
  private CardScrollView     mScrollView;
  private NotesScrollAdapter mAdapter;
  private NoteList           mNotes;
  private Location           mCurrentLocation;
  private List<CardBuilder>  mCards;
  private boolean            mEmptyCardsList;

  class NotesScrollAdapter
    extends CardScrollAdapter
  {
    @Override
    public int getPosition(Object item) {
      return mCards.indexOf(item);
    }
    @Override
    public int getCount() {
      return mCards.size();
    }
    @Override
    public Object getItem(int position) {
      return mCards.get(position);
    }
    @Override
    public int getViewTypeCount() {
      return CardBuilder.getViewTypeCount();
    }
    @Override
    public int getItemViewType(int position){
      return mCards.get(position).getItemViewType();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      return mCards.get(position).getView(convertView, parent);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().requestFeature( WindowUtils.FEATURE_VOICE_COMMANDS );
    mGestureDetector = new GestureDetector(this).setBaseListener(this);
    mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    mLocationManager = setupLocationManager();
    mNotes = new NoteList(this);
    mCards = new ArrayList<>();
    for( Note note : mNotes ) {
      addNoteCardToAdapter( note );
    }
    if( mEmptyCardsList ) {
      mCards.add( new CardBuilder(this, Layout.TEXT).setText("[tap to add a note]") );
    }
    mAdapter = new NotesScrollAdapter();
    mScrollView = createCardScrollView( mGestureDetector, mAudioManager );
    setContentView( mScrollView );
  }

  private void addNoteCardToAdapter( Note note ) {
    CardBuilder cb = new CardBuilder( this, Layout.TEXT );
    cb.setText( note.getBody() );
    if( mEmptyCardsList ) {
      mCards.clear();
      mEmptyCardsList = false;
    }
    mCards.add( cb );
  }

  private CardScrollView createCardScrollView(final GestureDetector gd,
                                              final AudioManager am)
  {
    CardScrollView scrollView = new CardScrollView(this) {
      @Override
      public boolean dispatchGenericFocusedEvent( MotionEvent event ) {
        if( gd.onMotionEvent( event )) return true;
        return super.dispatchGenericFocusedEvent( event );
      }
    };
    scrollView.setAdapter( mAdapter );
    scrollView.setHorizontalScrollBarEnabled( true );
    scrollView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        am.playSoundEffect( Sounds.TAP );
      }
    } );
    return scrollView;
  }

  @Override
  public boolean onCreatePanelMenu(int featureId, Menu menu) {
    if( featureId == WindowUtils.FEATURE_VOICE_COMMANDS ) {
      getMenuInflater().inflate( R.menu.main, menu );
    }
    // Pass through to super to setup touch menu.
    return super.onCreatePanelMenu(featureId, menu);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onMenuItemSelected( int featureId, MenuItem item ) {
    if( featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
        featureId == Window.FEATURE_OPTIONS_PANEL ) {
      int selectedNotePos = mScrollView.getSelectedItemPosition();
      Note currentNote = null;
      // HACK: poor man's note position retrieval, better to keep track
      int pos = 0;
      for( Note note : mNotes.allNotes().values() ) {
        if( selectedNotePos < pos++ ) continue;
        currentNote = note;
        break;
      }

      switch( item.getItemId() ) {
      case R.id.action_new_note:
        startSpeechPrompt();
        return true;
      case R.id.action_delete_note:
        if( currentNote != null ) {
          mNotes.deleteNote( currentNote.getId() );
          mCards.remove( selectedNotePos );
          mAdapter.notifyDataSetChanged();
        }
        return true;
      case R.id.action_map:
        if( currentNote != null ) {
          String nav = String.format("google.navigation:ll=%s,%s&mode=mru",
              currentNote.getLatitude(), currentNote.getLongitude() );
          Intent mapIntent = new Intent( Intent.ACTION_VIEW, Uri.parse(nav) );
          startActivity( mapIntent );
        }
        return true;
      }
    }
    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public boolean onGenericMotionEvent(MotionEvent event) {
    return mGestureDetector.onMotionEvent(event);
  }

  @Override
  public boolean onGesture( Gesture gesture ) {
    switch( gesture ) {
    case TAP:
      mAudioManager.playSoundEffect( Sounds.TAP );
      openOptionsMenu();
      return true;
    default:
      return false;
    }
  }

  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent intent)
  {
    if( resultCode == RESULT_OK && resultCode == SPEECH_REQ_CODE ) {
      List<String> results =
          intent.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
      if( !results.isEmpty() ) {
        String body = results.get( 0 );
        Note note = new Note()
          .setBody( body );
        if( mCurrentLocation != null ) {
          note.setLatitude( mCurrentLocation.getLatitude() );
          note.setLongitude( mCurrentLocation.getLongitude() );
        }
        mNotes.addNote( note );
        addNoteCardToAdapter( note );
        mAdapter.notifyDataSetChanged();
      }
    }
    super.onActivityResult(requestCode, resultCode, intent);
  }

  private void startSpeechPrompt() {
    Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
    startActivityForResult( intent, SPEECH_REQ_CODE );
  }

  private LocationManager setupLocationManager() {
    LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    criteria.setAccuracy( Criteria.ACCURACY_COARSE );
    List<String> providers = mLocationManager.getProviders(criteria, true);
    for( String provider : providers ) {
      locationManager.requestLocationUpdates(provider, 15*1000, 100, this);
    }
    return locationManager;
  }

  @Override
  public void onStatusChanged(String provider, int stat, Bundle extras) { }

  @Override
  public void onProviderEnabled(String provider) { }

  @Override
  public void onProviderDisabled(String provider) { }

  @Override
  public void onLocationChanged(Location location) {
    mCurrentLocation = location;
  }
}