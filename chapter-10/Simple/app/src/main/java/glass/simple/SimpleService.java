package glass.simple;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

// START:main
public class SimpleService extends Service {
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Toast.makeText(this, "Hello from a Service", Toast.LENGTH_LONG).show();
    return START_NOT_STICKY;
  }
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
// END:main