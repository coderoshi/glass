package glass.misspitts;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

// START:music
public class Music implements OnPreparedListener, OnErrorListener {
  // END:music
  private MediaPlayer mediaPlayer;
  private AtomicBoolean isMusicPrepared;

  // START:music
  public Music( Activity activity, String fileName ) {
    mediaPlayer = new MediaPlayer();
    isMusicPrepared = new AtomicBoolean();
    initialize( activity, fileName );
  }
  public void play() {
    if( !isMusicPrepared.get() ) {
      mediaPlayer.prepareAsync();
    } else {
      mediaPlayer.start();
    }
  }
  public void stop() {
    if( isMusicPrepared.compareAndSet(true,false)) {
      mediaPlayer.stop();
    }
  }
  public void release() {
    stop();
    mediaPlayer.release();
  }
  // ...
  // END:music

  // START:inits
  private void initialize( Activity activity, String fileName ) {
    AssetFileDescriptor afd = null;
    // END:inits
    try {
    // START:inits
    afd = activity.getAssets().openFd( fileName );
    mediaPlayer.setDataSource(afd.getFileDescriptor(),
                              afd.getStartOffset(), afd.getLength());
    mediaPlayer.setLooping( true );
    mediaPlayer.setOnPreparedListener( this );
    // END:inits
    mediaPlayer.setOnErrorListener( this );
    } catch( IOException e ) {
      throw new RuntimeException("Failed to load music file " + fileName, e);
    } finally {
      try {
        if( afd != null ) afd.close();
      } catch(IOException e) {}
    }
    // START:inits
  }
  @Override
  public void onPrepared( MediaPlayer mp ) {
    isMusicPrepared.set( true );
    // when the music is prepared, just start playing it
    mediaPlayer.start();
  }
  // END:inits

  @Override
  public boolean onError(MediaPlayer mp, int what, int extra) {
    Log.e("glass.misspitts", "Music error. What:"+what+", Extra:"+extra);
    return false;
  }
}
