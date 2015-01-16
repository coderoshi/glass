package glass.partify;

import static glass.partify.Log.d;

import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import com.google.android.glass.timeline.DirectRenderingCallback;

// START:interface
public class LiveCardRenderer implements DirectRenderingCallback {

    public static final int DEFAULT_BALLOON_COUNT = 3;
//END:interface
    /**
     * This is a thread that runs in a loop, and will continue drawing frames until
     * it's told to pause or stop entirely.
     */
    // START:drawframe
    class DrawFrameThread extends Thread {
        private static final int FPS = 24;
        private boolean running;
        private boolean pause;
        public void run() {
            // END:drawframe
            d("startRun");
            // START:drawframe
            pause = false;
            running = true;
            while( running ) {
                long frameStart = SystemClock.elapsedRealtime();
                if( !pause )  draw();
                long frameLength = SystemClock.elapsedRealtime() - frameStart;
                long sleepTime = 1000 / FPS - frameLength;
                if (sleepTime > 0) {
                  SystemClock.sleep(sleepTime);
                }
            }
        }
        public void pause(boolean pause) {
            this.pause = pause;
        }
        public void exit() {
            pause = true;
            running = false;
            // END:drawframe
            while( true ) {
                try {
                    // START:drawframe
                    join();
                    // END:drawframe
                    break;
                } catch (InterruptedException e) {}
            }
            // START:drawframe
        }
    }
    // END:drawframe

    // START:interface
    private Context         context;
    private SurfaceHolder   holder;
    private DrawFrameThread thread;
    // END:interface
    private Bitmap          backgroundBitmap;
    // START:loadBalloons
    private Balloon[] balloons;
    // END:loadBalloons

    // START:interface
    public LiveCardRenderer(Context context) {
        this.context = context;
    }
    public void surfaceCreated(SurfaceHolder holder) {
        // END:interface
        d("surfaceCreated");
        // START:interface
        this.holder = holder;
        this.thread = new DrawFrameThread();
        // END:interface
        try {
            // START:interface
            loadBalloons(DEFAULT_BALLOON_COUNT);
            // END:interface
        } catch(IOException e) {
            e.printStackTrace();
        }
        // START:interface
        this.thread.start();
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // nothing to do here
        // END:interface
        d("surfaceChanged");
        // START:interface
    }
    // START:interface
    public void renderingPaused(SurfaceHolder surfaceholder, boolean pause) {
        // END:interface
        d("renderingPaused " + pause);
        // START:interface
        this.thread.pause(pause);
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // END:interface
        d("surfaceDestroyed");
        // START:interface
        this.thread.exit();
        this.thread = null;
        this.holder = null;
    }
    // there's more code coming...
    // END:interface

    // START:camera
    public void setBackgroundImage(Bitmap background) {
        this.backgroundBitmap = background;
        // END:camera
        d("backgroundBitmap " + (this.backgroundBitmap != null));
        // START:camera
    }

    // END:camera

    // START:loadBalloons
    public synchronized void loadBalloons(int balloonCount) throws IOException {
        if( thread != null )  thread.pause(true);
        balloons = new Balloon[balloonCount];
        Random rand = new Random();
        for( int i = 0; i < balloonCount; i++ ) {
            // what color balloon
            Balloon.Color[] colors = Balloon.Color.values();
            String color = colors[rand.nextInt(colors.length)].toString();
            // what size should the balloon be
            double percentSize = rand.nextDouble();
            // how far left to does the balloon rise
            int left = rand.nextInt(640);
            balloons[i] = new Balloon(context, color, percentSize, left, 360);
        }
        if( thread != null )  thread.pause(false);
    }
    // END:loadBalloons

    // START:camera
    // START:draw
    private void draw() {
        // END:camera
        if( this.balloons == null ) { return; }
        final Canvas canvas;
        try {
            canvas = this.holder.lockCanvas();
        } catch (Exception e) { return; }
        if( canvas == null ) { return; }
        synchronized( LiveCardRenderer.this ) {
            canvas.drawColor( Color.BLACK );
            // END:draw
            // START:camera
            // ...after we paint the canvas black with drawColor...
            if( this.backgroundBitmap != null ) {
                canvas.drawBitmap( this.backgroundBitmap, 0, 0, null );
            }
            // ...now render the balloons overtop the image...
            // END:camera
            // START:draw
            for( int i = 0; i < this.balloons.length; i++ ) {
                Balloon b = this.balloons[i];
                if( b.getBitmap() != null ) {
                    canvas.drawBitmap( b.getBitmap(), b.nextLeft(), b.nextTop(), null );
                }
            }
        }
        this.holder.unlockCanvasAndPost( canvas );
        // START:camera
    }
    // END:camera
    // END:draw
}
