package glass.qr;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.ViewAsserts;
import android.widget.FrameLayout;

// START:setup
public class QRCameraActivityTest
  extends ActivityUnitTestCase<QRCameraActivity>
{
  private QRCameraActivity activity;
  public QRCameraActivityTest() {
    super( QRCameraActivity.class );
  }
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Intent intent =
        new Intent(getInstrumentation().getTargetContext(), QRCameraActivity.class);
    activity = startActivity(intent, null, null);
    assertNotNull( activity );
  }
// END:setup

// START:layout
  public void testContentLayout() {
    // Get the activity's content view
    FrameLayout cv = (FrameLayout)activity.findViewById( android.R.id.content );
    FrameLayout frameLayout = (FrameLayout)cv.getChildAt(0);
    // Get the QRCameraView and overlay
    assertEquals( 2, frameLayout.getChildCount() );
    assertTrue( frameLayout.getChildAt(0) instanceof QRCameraView );
    assertTrue( frameLayout.getChildAt(1) instanceof QRCameraActivity.OverlayView );
    // test that QRCameraView is displayed on the screen
    ViewAsserts.assertOnScreen( cv, frameLayout.getChildAt(0) );
  }
// END:layout
}
