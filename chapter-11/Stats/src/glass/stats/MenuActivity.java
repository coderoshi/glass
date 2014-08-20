package glass.stats;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MenuActivity extends Activity
{
  // START:attachedToWindow
  @Override
  public void onAttachedToWindow() {
      super.onAttachedToWindow();
      openOptionsMenu();
  }
  // END:attachedToWindow

  // START:createmenu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.stats, menu);
    return true;
  }
  // END:createmenu
  
  // START:itemselected
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.stop:
      return stopService(new Intent(this, StatsService.class));
    default:
      return super.onOptionsItemSelected(item);
    }
  }
  // END:itemselected

  // START:openclose
  @Override
  public void onOptionsMenuClosed(Menu menu) {
    finish();
  }
  // END:openclose
}
