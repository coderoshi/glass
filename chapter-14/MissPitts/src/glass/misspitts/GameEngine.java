package glass.misspitts;

import glass.misspitts.UserInputManager.GameEvent;
import glass.misspitts.objects.Block;
import glass.misspitts.objects.Level;
import glass.misspitts.objects.MissPitts;
import glass.misspitts.objects.Score;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Handles the state of the game, game objects, and screen paint state
 */
// START:init
public class GameEngine {
  enum GameState { RUNNING, GAME_OVER }
  // END:init
  private float SFX_VOLUME = 0.60f;
  private static Level currentLevel, nextLevel;

  private Activity     activity;
  private MissPitts    missPitts;
  private Score        score;
  private UserInputManager inputManager;
  private Canvas       canvasBuffer;
  private GameState    state;
  private Music        themeMusic;
  private SoundPool    soundPool;
  private int          jumpSoundId, endSoundId;

  public static void setupNextLevel() {
    currentLevel = nextLevel;
    nextLevel = currentLevel.next();
  }

  // START:init
  public GameEngine( Activity activity, UserInputManager inputManager,
                     Bitmap frameBuffer ) {
    this.activity = activity;
    this.inputManager = inputManager; 
    canvasBuffer = new Canvas( frameBuffer );
    initGameAssets();
    score = new Score();
    missPitts = new MissPitts( score );
    currentLevel = new Level( 0, 0, missPitts );
    nextLevel = currentLevel.next();
    state = GameState.RUNNING;
    themeMusic.play();
  }
  // END:init
  // START:load
  private void initGameAssets() {
    MissPitts.bitmapAsset1 = loadImage("pitts1.png");
    MissPitts.bitmapAsset2 = loadImage("pitts2.png");
    Block.bitmapAsset      = loadImage("block.png");
    // START:soundpool2
    soundPool = new SoundPool( 4, AudioManager.STREAM_MUSIC, 0 );
    jumpSoundId = loadSoundPoolId( "jump.wav" );
    endSoundId = loadSoundPoolId( "end.wav" );
    // END:soundpool2
    themeMusic = new Music( activity, "theme.mp3" );
  }
  // END:load
  // Have a public "load assets" option that replays the music
  // START:soundrelease
  public void releaseAssets() {
    themeMusic.release();
    soundPool.unload( jumpSoundId );
    soundPool.unload( endSoundId );
  }
  // END:soundrelease

  // START:update1
  public void update() {
    switch (state) {
    case RUNNING:
      updateFrame();
      break;
    case GAME_OVER:
      releaseAssets();
      activity.finish();
      break;
    }
  }
  // END:update1

  // START:update2
  private void updateFrame() {
    List<GameEvent> gestureEvents = inputManager.getEvents();
    // Handle player events, jumps/walks
    for( int i = 0; i < gestureEvents.size(); i++ ) {
      switch( gestureEvents.get( i ) ) {
      case JUMP:
        soundPool.play( jumpSoundId, SFX_VOLUME, SFX_VOLUME, 0, 0, 1 );
        missPitts.jump( MissPitts.SINGLE_JUMP );     break;
      case JUMP_HIGHER:
        soundPool.play( jumpSoundId, SFX_VOLUME, SFX_VOLUME, 0, 0, 1 );
        missPitts.jump( MissPitts.DOUBLE_JUMP );     break;
      case JUMP_HIGHEST:
        soundPool.play( jumpSoundId, SFX_VOLUME, SFX_VOLUME, 0, 0, 1 );
        missPitts.jump( MissPitts.TRIPLE_JUMP );     break;
      case WALK:
        missPitts.moveRight( MissPitts.WALK_SPEED ); break;
      case RUN:
        missPitts.moveRight( MissPitts.RUN_SPEED );  break;
      case STOP:
        missPitts.stopRight();  break;
      }
    }
    // Update the player position and relative movement of the levels/blocks
    missPitts.update();
    checkGameOver();
    currentLevel.update();
    nextLevel.update();
  }
  private void checkGameOver() {
    if( missPitts.getCenterY() > 500 ) {
      // fell down a pit :(
      soundPool.play( endSoundId, SFX_VOLUME, SFX_VOLUME, 1, 0, 1 );
      state = GameState.GAME_OVER;
    }
  }
  // END:update2

  /** Paint all objects into the frame buffer via Canvas */
  // START:paint
  public void paint() {
    // clear the buffer with a blue sky color
    canvasBuffer.drawColor( 0xFF22AAAA );
    // draw the blocks
    currentLevel.paint( canvasBuffer );
    nextLevel.paint( canvasBuffer );
    // draw Miss Pitts
    missPitts.paint( canvasBuffer );
    // Paint the score mid screen
    score.paint( canvasBuffer );
  }
  // END:paint

  // START:load
  private Bitmap loadImage( String fileName ) {
    // END:load
    try {
    // START:load
    InputStream in = activity.getAssets().open(fileName);
    return BitmapFactory.decodeStream( in );
    // END:load
    } catch( IOException e ) {
      throw new RuntimeException("Failed loading bitmap " + fileName);
    }
    // START:load
  }
  // END:load

  // START:soundpool1
  private int loadSoundPoolId( String fileName ) {
    try {
      AssetFileDescriptor assetDescriptor = activity.getAssets().openFd( fileName );
      return soundPool.load( assetDescriptor, 0 );
    } catch (IOException e) {
      throw new RuntimeException("Failed loading sound fx " + fileName);
    }
  }
  // END:soundpool1

  public Score getScore() {
    return score;
  }
}