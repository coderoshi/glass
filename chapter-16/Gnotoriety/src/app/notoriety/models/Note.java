package app.notoriety.models;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
// START:note
public class Note implements Serializable {
  String id;
  String body;
  Date createdAt;
  double latitude;
  double longitude;
// END:note

  public Note() {
    createdAt = new Date();
  }
  public Note setId(String id) {
    this.id = id;
    return this;
  }
  public String getId() {
    return id;
  }
  public Note setBody(String body) {
    this.body = body;
    return this;
  }
  public Note setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
    return this;
  }
  public Note setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }
  public Note setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }
  public String getBody() {
    return body;
  }
  public Date getCreatedAt() {
    return createdAt;
  }
  public double getLatitude() {
    return latitude;
  }
  public double getLongitude() {
    return longitude;
  }
  public String getTitle() {
    return body.substring(0, Math.min(body.length(), 25)).replaceAll("(\\n|\\t|\\s)+", " ");
  }
  public String toString() {
    return getTitle();
  }
// START:note
}
// END:note