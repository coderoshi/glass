package glass.misspitts.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Score implements GameObject {
  private int   value;
  private Paint paint;
  // START:score
  public Score() {
    value = 0;
    paint = new Paint();
    paint.setTextSize( 100 );
    paint.setTextAlign( Paint.Align.CENTER );
    paint.setAntiAlias( true );
    paint.setColor( Color.WHITE );
  }
  public void update() {
    value++;
  }
  public void paint( Canvas c ) {
    c.drawText( Integer.toString(value), 350, 220, paint );
  }
  // END:score

  public int getValue() {
    return value;
  }
}
