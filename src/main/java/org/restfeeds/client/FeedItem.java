package org.restfeeds.client;

import java.util.StringJoiner;

public class FeedItem {

  private String id;
  private String next;
  private String type;
  private String resource;
  private String method;
  private String timestamp;
  private Object data;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
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
