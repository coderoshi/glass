package test.book.glass;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import test.book.glass.blog.models.Blog;
import test.book.glass.blog.models.User;

public final class SessionUtils
{
  public static boolean isLoggedIn(HttpServletRequest request) {
    return getUser(request) != null;
  }

  public static User getUser(HttpServletRequest request) {
    return (User)request.getSession().getAttribute("user");
  }

  public static String getUserId(HttpServletRequest request) {
    User user = getUser(request);
    if( user != null ){
      return user.getId();
    }
    return null;
  }

  public static void setUser(HttpServletRequest request, User user) {
    request.getSession().setAttribute("user", user);
  }

  public static void removeUser(HttpServletRequest request) throws IOException {
    // Remove their ID from the local session
    request.getSession().removeAttribute("user");
  }

  public static Blog getBlog(HttpServletRequest request) {
    return (Blog)request.getSession().getAttribute("blog");
  }

  public static void setBlog(HttpServletRequest request, Blog blog) {
    request.getSession().setAttribute("blog", blog);
  }

  public static void removeBlog(HttpServletRequest request) throws IOException {
    request.getSession().removeAttribute("blog");
  }
}
