package glass.misspitts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
// START:render
public class RenderView extends SurfaceView implements Runnable {
  public static final int FPS = 48;
  public static final int MSPF = 1000 / FPS;
  // END:render
  private GameEngine    engine;
  private Bitmap        frameBuffer;
  private Thread        renderThread;
  private SurfaceHolder holder;
  private volatile boolean running;

  public RenderView( Context context, GameEngine engine, Bitmap frameBuffer ) {
    super( context );
    this.engine = engine;
    this.frameBuffer = frameBuffer;
    this.holder = getHolder();
  }

  // START:render
  public void run() {
    Rect dest = new Rect();
    while( running ) {
      if( !holder.getSurface().isValid() ) continue;
      long startTime = System.currentTimeMillis();
      // update the game engine, paint the buffer, and draw it on the canvas
      engine.update();
      engine.paint();
      Canvas canvas = holder.lockCanvas();
      canvas.getClipBounds( dest );
      canvas.drawBitmap( frameBuffer, null, dest, null );
      holder.unlockCanvasAndPost( canvas );
      // fill in the difference between render time and frame timing.
      // this normalizes the framerate to about 48/sec
      int delta = MSPF - (int)(System.currentTimeMillis() - startTime);
      if( delta > 0 ) {
        try {
          Thread.sleep( delta );
        } catch (InterruptedException e) {}
      }
    }
  }
  // END:render

  // START:running
  public void resume() {
    running = true;
    renderThread = new Thread( this );
    renderThread.start();
  }
  public void pause() {
    running = false;
    // END:running
    // keep attempting to rejoin the thread until it works
    while( true ) {
    try {
    // START:running
    renderThread.join();
    // END:running
    break;
    } catch (InterruptedException e) {}
    }
    // START:running
  }
  // END:running
}