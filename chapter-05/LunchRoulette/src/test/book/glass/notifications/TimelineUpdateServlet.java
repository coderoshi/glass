package test.book.glass.notifications;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import test.book.glass.LunchRoulette;
import test.book.glass.MirrorUtils;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.Notification;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.UserAction;

@SuppressWarnings("serial")
// START:timelineupdate
public class TimelineUpdateServlet extends HttpServlet
{
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException
  {
    // Generate Notification from request body
    JsonFactory jsonFactory = new JacksonFactory();
    Notification notification =
      jsonFactory.fromInputStream( req.getInputStream(), Notification.class );

    // Get this user action's type
    String userActionType = null;
    if( !notification.getUserActions().isEmpty() )
      userActionType = notification.getUserActions().get(0).getType();

    // If this is a pinned timeline item, log who and which timeline item
    if( "timeline".equals( notification.getCollection() )
        && "UPDATE".equals( notification.getOperation() ) 
        && "PIN".equals( userActionType ) )
    {
      String userId = notification.getUserToken();
      String itemId = notification.getItemId();

      System.out.format( "User %s pinned %s", userId, itemId );
    }
    // END:timelineupdate
    // START:custom1
    else if( "timeline".equals( notification.getCollection() )
        && "UPDATE".equals( notification.getOperation() )
        && "CUSTOM".equals( userActionType ) )
    {
    // END:custom1
    // START:custom2
      UserAction userAction = notification.getUserActions().get(0);
      if( "ALT".equals( userAction.getPayload() ) )
      {
        // Add a new timeline item, and bundle it to the previous one
        String userId = notification.getUserToken();
        String itemId = notification.getItemId();
        Mirror mirror = MirrorUtils.getMirror( userId );
        Timeline timeline = mirror.timeline();
        // Get the timeline item that owns the tapped menu
        TimelineItem current = timeline.get( itemId ).execute();
        String bundleId = current.getBundleId();
        // If not a bundle, update this item as a bundle
        if( bundleId == null ) {
          bundleId = "lunchRoulette" + UUID.randomUUID();
          current.setBundleId( bundleId );
          timeline.update( itemId, current).execute();
        }
        // Create a new random restaurant suggestion
        TimelineItem newTi = 
          LunchRoulette.buildRandomRestaurantTimelineItem( getServletContext(), userId );
        newTi.setBundleId( bundleId );
        timeline.insert( newTi ).execute();
      }
     // END:custom2
    }
  }
}
