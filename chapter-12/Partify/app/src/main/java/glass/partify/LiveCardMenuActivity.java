package glass.partify;

import static glass.partify.Log.d;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.glass.content.Intents;

public class LiveCardMenuActivity extends Activity {
    // START:ballooncount1
    private static final int  REQ_CODE_BALLOON_COUNT = 100;

    // END:ballooncount1
    // START:camera1
    private static final int  REQ_CODE_TAKE_PICTURE  = 101;

    // END:camera1

    private FileObserver      observer;
    private boolean           waitingForResult;
    private PartyService      service;
    // START:serviceconn
    private ServiceConnection serviceConnection =
            new ServiceConnection() {
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    if (binder instanceof PartyService.GifferBinder) {
                        service = ((PartyService.GifferBinder) binder).getService();
                        if( hasWindowFocus() ) {
                            openOptionsMenu();
                        }
                    }
                    unbindService(this);
                }
                @Override
                public void onServiceDisconnected(ComponentName name) {}
            };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService(new Intent(this, PartyService.class), serviceConnection, 0);
    }
    // END:serviceconn

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.party, menu);
        return true;
    }

    @Override
    // START:ballooncount1
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop:
                return stopService(new Intent(this, PartyService.class));
            // END:ballooncount1
            // START:camera1
            case R.id.take_picture:
                Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureImageIntent, REQ_CODE_TAKE_PICTURE);
                waitingForResult = true;
                return true;
            // END:camera1
            // START:ballooncount1
            case R.id.balloon_count:
                Intent balloonCountI = new Intent(this, BalloonCountActivity.class);
                balloonCountI.putExtra(
                        BalloonCountActivity.EXTRA_CURRENT_COUNT,
                        service.getBalloonCount());
                startActivityForResult(balloonCountI, REQ_CODE_BALLOON_COUNT);
                waitingForResult = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // END:ballooncount1

    @Override
    // START:ballooncount2
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        // END:ballooncount2
        d("onActivityResult");
        // START:ballooncount2
        if( resultCode != RESULT_OK ) {
            finish();
            return;
        }
        switch( requestCode ) {
            // END:ballooncount2
            // START:camera2
            case REQ_CODE_TAKE_PICTURE:
                String picFilePath =
                        intent.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);
                final File pictureFile = new File(picFilePath);
                final String picFileName = pictureFile.getName();
                // set up a file observer to watch this directory on sd card
                observer = new FileObserver(pictureFile.getParentFile().getAbsolutePath()){
                    public void onEvent(int event, String file) {
                        // END:camera2
                        d("File " + file + ", event " + event);
                        // START:camera2
                        if( event == FileObserver.CLOSE_WRITE && file.equals(picFileName) ) {
                            // END:camera2
                            d("Image file written " + file);
                            // START:camera2
                            service.setImageFileName(pictureFile.getAbsolutePath());
                            stopWatching();
                            waitingForResult = false;
                            finish();
                        }
                    }
                };
                observer.startWatching();
                waitingForResult = false;
                // END:camera2
                return;
            // START:ballooncount2
            case REQ_CODE_BALLOON_COUNT:
                int balloonCount =
                  intent.getIntExtra(BalloonCountActivity.EXTRA_BALLOON_COUNT, 3);
                service.setBalloonCount(balloonCount);
                waitingForResult = false;
                finish();
                // END:ballooncount2
                return;
            default:
                finish();
        }
        // START:ballooncount2
    }
    // END:ballooncount2

    @Override
    // START:close
    public void onOptionsMenuClosed(Menu menu) {
        if( !waitingForResult ) {
            finish();
        }
    }
    // END:close
}
