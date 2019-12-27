package org.restfeeds.client.spring;

import org.restfeeds.client.FeedItem;
import org.restfeeds.client.FeedItemConsumer;
import org.springframework.stereotype.Component;

@Component
public class SimpleFeedItemConsumer implements FeedItemConsumer {

  @Override
  public void accept(FeedItem feedItem) {
    System.out.println(feedItem);
  }
}
