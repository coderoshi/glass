package glass.stats;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.google.android.glass.timeline.LiveCard;

// START:setup
public class StatsServiceTest
    extends ServiceTestCase<StatsService>
{
    private StatsService service;
    public StatsServiceTest() {
        super( StatsService.class );
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent( getContext(), StatsService.class );
        startService( intent );
        service = getService();
        assertNotNull( service );
    }
// END:setup

// START:test1
    public void testLiveCardIsPublished() {
        LiveCard liveCard = service.getLiveCard();
        assertNotNull( liveCard );
        assertTrue( liveCard.isPublished() );
    }
}
// END:test1