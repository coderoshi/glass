package glass.simple;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

// START:main
public class SimpleReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    String message = "I shouldn't exist";
    if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
      message = "Yum! Power tastes good.";
    } else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
      message = "Being unplugged makes me hangry.";
    }
    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
  }
}
//END:main