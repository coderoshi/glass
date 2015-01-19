package glass.qr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.test.AndroidTestCase;

// START:ib
public class QRIntentBuilderTest
  extends AndroidTestCase
{
  public void testWeb() {
    QRIntentBuilder ib = new QRIntentBuilder("http://pragprog.com");
    Intent intent = ib.buildIntent();
    assertNotNull( intent );
    assertEquals( Intent.ACTION_VIEW, intent.getAction() );
    Uri uri = intent.getData();
    assertNotNull( uri );
    assertEquals( "http", uri.getScheme() );
    assertEquals( "pragprog.com", uri.getHost() );
  }
// END:ib

  public void testLocation() {
    String ll = "45.5200,-122.6819";
    QRIntentBuilder ib = new QRIntentBuilder(ll);
    Intent intent = ib.buildIntent();
    assertNotNull( intent );
    assertEquals( Intent.ACTION_VIEW, intent.getAction() );
    Uri uri = intent.getData();
    assertNotNull( uri );
    assertEquals( "google.navigation", uri.getScheme() );
    assertEquals( "google.navigation:ll="+ll+"&mode=mru", uri.toString() );
  }

  public void testText() {
    String message = "Hello there!";
    QRIntentBuilder ib = new QRIntentBuilder(message);
    Intent intent = ib.buildIntent();
    assertNotNull( intent );
    Bundle extras = intent.getExtras();
    assertNotNull( extras );
    assertEquals( message, extras.get("text") );
  }
}