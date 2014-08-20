package glass.nevermind;

import android.app.Activity;
import android.os.Bundle;

/**
 * Does nothing, just closes.
 * @author Eric Redmond
 * @twitter coderoshi
 */
// START:cancel
public class CancelActivity extends Activity {
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    finish();
  }
}
//END:cancel
