package org.restfeeds.client;

import java.util.StringJoiner;

public class FeedItem {

  private final String id;
  private final String next;
  private final String type;
  private final String resource;
  private final String method;
  private final String timestamp;
  private final Object data;

  public FeedItem(String id, String next, String type, String resource, String method,
      String timestamp, Object data) {
    this.id = id;
    this.next = next;
    this.type = type;
    this.resource = resource;
    this.method = method;
    this.timestamp = timestamp;
    this.data = data;
  }

  public String getId() {
    return id;
  }

  public String getNext() {
    return next;
  }

  public String getType() {
    return type;
  }

  public String getResource() {
    return resource;
  }

  public String getMethod() {
    return method;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public Object getData() {
    return data;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FeedItem.class.getSimpleName() + "[", "]")
        .add("id='" + id + "'")
        .add("next='" + next + "'")
        .add("type='" + type + "'")
        .add("resource='" + resource + "'")
        .add("method='" + method + "'")
        .add("timestamp='" + timestamp + "'")
        .add("data=" + data)
        .toString();
  }
}
