package test.book.glass.blog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import test.book.glass.SessionUtils;
import test.book.glass.ViewUtils;
import test.book.glass.auth.BaseServlet;
import test.book.glass.blog.models.Blog;
import test.book.glass.blog.models.User;

@SuppressWarnings("serial")
public class FollowedServlet extends BaseServlet
{
  /**
   * Lists most recent posts for all blogs that the current user follows
   */
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
  {
    User user = SessionUtils.getUser( req );
    if( user == null ) {
      res.sendRedirect( "/" );
      return;
    }
    int page = ViewUtils.getPage( req );

    Map<String, Object> data = new HashMap<String, Object>();
    data.put( "pg", user.getFollowedPosts( page ) );
    data.put( "user", user );
    render( res, "followed.ftl", data );
  }

  /**
   * Follows the given blog
   */
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
  {
    User user = SessionUtils.getUser( req );
    if( user == null ) {
      res.sendRedirect( "/" );
      return;
    }

    // follow the given post. if it doesn't exist, nothing to do here.
    String nickname = ViewUtils.extractFromPath( req );
    Blog blog = Blog.get( nickname );
    if( blog == null ) {
      res.setStatus( 404 );
      res.getWriter().append( "No such blog exists" );
      return;
    }

    user.followBlog( nickname );
    user.save();
  }
}
