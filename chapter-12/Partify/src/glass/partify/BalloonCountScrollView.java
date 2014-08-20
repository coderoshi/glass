package glass.partify;

import android.content.Context;
import android.view.MotionEvent;

import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;

// START:view
public class BalloonCountScrollView extends CardScrollView {
  // NOTE: this is not a android.view.GestureDetector class
  private GestureDetector gestureDetector;
  
  public BalloonCountScrollView(Context context, GestureDetector gestureDetector) {
    super( context );
    this.gestureDetector = gestureDetector;
  }
  
  public boolean dispatchGenericFocusedEvent(MotionEvent event) {
    if( gestureDetector.onMotionEvent(event) ) { return true; }
    return super.dispatchGenericFocusedEvent(event);
  }
}
// END:view