package glass.simple;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;

import com.google.android.glass.widget.CardBuilder;

// START:main
public class SimpleActivity extends Activity {
    private View cardView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Hello World")
                .setFootnote("This is from an Activity");
        cardView = card.getView();
        setContentView(cardView);
    }
    // END:main

    // START:provider
    public static final String[] EVENT_PROJECTION = new String[] {
        CalendarContract.Calendars._ID,                   // 0
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME  // 1
    };
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;
    protected void onResume() {
        super.onResume();
        ContentResolver cr = getContentResolver();
        // The URI is "content://com.android.calendar/calendars"
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "(" + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?)";
        String[] selectionArgs = new String[] {"eric.redmond@gmail.com"};
        // Submit the query and get a Cursor object back.
        Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        // Use the cursor to step through the returned records
        while( cur.moveToNext() ) {
            // Get the field values
            long calID = cur.getLong( PROJECTION_ID_INDEX );
            String displayName = cur.getString( PROJECTION_DISPLAY_NAME_INDEX );
            Log.i("glass.simple", "calID: " + calID + ", displayName: " + displayName);
        }
    }
    // END:provider
    // START:main
    protected void onDestroy() {
        super.onDestroy();
        cardView = null;
    }
}
//END:main