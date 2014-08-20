package glass.misspitts;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

// extends OrientationEventListener?

// START:tap
public class UserInputManager
  implements GestureDetector.BaseListener, SensorEventListener
{
  enum GameEvent {
    JUMP, JUMP_HIGHER, JUMP_HIGHEST,
    WALK, RUN, STOP
  }
  // END:tap
  private Context          context;
  private GestureDetector  gestureDetector;
  private SensorManager    sensorManager;
  private List<GameEvent> events;
  private List<GameEvent> eventsBuffer;
  // only add events if the movement changes from the last registered one
  private GameEvent       lastMovement = GameEvent.STOP;

  // START:tap
  public UserInputManager( Context context ) {
    this.context = context;
    events = new ArrayList<GameEvent>(4);
    eventsBuffer = new ArrayList<GameEvent>(4);
    gestureDetector = new GestureDetector( context ).setBaseListener( this );
  }
  public synchronized List<GameEvent> getEvents() {
    events.clear();
    events.addAll( eventsBuffer );
//    if( !eventsBuffer.isEmpty() ) {
//      List<GameEvent> events = Collections.unmodifiableList( eventsBuffer );
      eventsBuffer.clear();
      return events;
//    } else {
//      return Collections.emptyList();
//    }
  }
  public boolean dispatchGenericFocusedEvent( MotionEvent event ) {
    return gestureDetector != null && gestureDetector.onMotionEvent(event);
  }
  @Override
  public boolean onGesture( Gesture gesture ) {
    switch( gesture ) {
    case TAP:
      eventsBuffer.add( GameEvent.JUMP );   return true;
    case TWO_TAP:
      eventsBuffer.add( GameEvent.JUMP_HIGHER );   return true;
    case THREE_TAP:
      eventsBuffer.add( GameEvent.JUMP_HIGHEST ); return true;
    default:
      return false;
    }
  }
  // ...
  // END:tap

  // sensor event listener (gravity orientation)

  // START:register
  public void registerSensor() {
    // register gravity sensor for player movement
    sensorManager = (SensorManager)context.getSystemService( Context.SENSOR_SERVICE );
    sensorManager.registerListener(this,
        sensorManager.getDefaultSensor( Sensor.TYPE_GRAVITY ),
        SensorManager.SENSOR_DELAY_GAME);
  }
  public void unregisterSensor() {
    sensorManager.unregisterListener( this );
  }
  // END:register

  @Override
  // START:sensor
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // nothing to do here
  }
  // END:sensor

  @Override
  // START:sensor
  public void onSensorChanged( SensorEvent event ) {
    if( event.sensor.getType() == Sensor.TYPE_GRAVITY ) {
      // convert Cartesian system to the polar value in spherical coords
      float x = event.values[0], y = event.values[1], z = event.values[2];
      // theta is the tilt of the Glass from a flat resting position 0 in radians
      // then convert to negative degrees for simplicity
      double thetaDegrees = -Math.toDegrees(
          Math.atan(x / Math.sqrt(Math.pow(y,2) + Math.pow(z,2))));
      if( thetaDegrees < 10 ) {       addEventChange( GameEvent.STOP ); }
      else if( thetaDegrees >= 20 ) { addEventChange( GameEvent.RUN );  }
      else if( thetaDegrees >= 10 ) { addEventChange( GameEvent.WALK ); }
    }
  }
  private void addEventChange( GameEvent event ) {
    if( event != lastMovement ) {
      lastMovement = event;
      eventsBuffer.add( event );
    }
  }
  // END:sensor
}