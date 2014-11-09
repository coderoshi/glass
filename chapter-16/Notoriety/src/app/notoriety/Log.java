package app.notoriety;

/**
 * A convenience logger that always logs to this package space.
 * @author ericredmond
 */
public class Log
{
  public static final String TAG = Log.class.getPackage().getName();

  public static final void d( String message ) {
    android.util.Log.d(TAG, message);
  }

  public static final void d( String message, Throwable t ) {
    android.util.Log.d(TAG, message, t);
  }

  public static final void d( String message, Object...args ) {
    android.util.Log.d(TAG, String.format(message, args));
  }

  public static final void i( String message ) {
    android.util.Log.i(TAG, message);
  }

  public static final void i( String message, Throwable t ) {
    android.util.Log.i(TAG, message, t);
  }

  public static final void i( String message, Object...args ) {
    android.util.Log.i(TAG, String.format(message, args));
  }

  public static final void w( String message ) {
    android.util.Log.w(TAG, message);
  }

  public static final void w( String message, Throwable t ) {
    android.util.Log.w(TAG, message, t);
  }

  public static final void w( String message, Object...args ) {
    android.util.Log.w(TAG, String.format(message, args));
  }

  public static final void e( String message ) {
    android.util.Log.e(TAG, message);
  }

  public static final void e( String message, Throwable t ) {
    android.util.Log.e(TAG, message, t);
  }

  public static final void e( String message, Object...args ) {
    android.util.Log.e(TAG, String.format(message, args));
  }

  public static final void v( String message ) {
    android.util.Log.v(TAG, message);
  }

  public static final void v( String message, Throwable t ) {
    android.util.Log.v(TAG, message, t);
  }

  public static final void v( String message, Object...args ) {
    android.util.Log.v(TAG, String.format(message, args));
  }

  public static final void wtf( String message ) {
    android.util.Log.wtf(TAG, message);
  }

  public static final void wtf( String message, Throwable t ) {
    android.util.Log.wtf(TAG, message, t);
  }

  public static final void wtf( String message, Object...args ) {
    android.util.Log.wtf(TAG, String.format(message, args));
  }
}