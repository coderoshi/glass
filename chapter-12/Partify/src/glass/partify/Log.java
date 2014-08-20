package glass.partify;

public final class Log {
	public final static String TAG = Log.class.getPackage().toString();

	public static void d(String message) {
		android.util.Log.d(TAG, message);
	}
}
