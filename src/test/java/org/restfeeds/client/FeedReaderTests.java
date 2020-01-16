package org.restfeeds.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class FeedReaderTests {

  @Test
  void shouldRead() throws Exception {
    String feed = "http://localhost/events";
    InMemoryNextLinkRepository nextLinkRepository = new InMemoryNextLinkRepository();
    AtomicInteger count = new AtomicInteger(0);
    FeedReader feedReader =
        new FeedReader(
            feed,
            feedItem -> count.incrementAndGet(),
            new DummyFeedReaderRestClient(),
            nextLinkRepository);

    new Thread(feedReader::read).start();

    Thread.sleep(300L);
    assertTrue(count.get() >= 2);
    assertEquals("http://localhost/events?offset=100", nextLinkRepository.get(feed).orElse(null));
  }

  @Test
  void shouldStop() throws Exception {
    AtomicInteger count = new AtomicInteger(0);
    FeedReader feedReader =
        new FeedReader(
            "http://localhost/events",
            feedItem -> count.incrementAndGet(),
            new DummyFeedReaderRestClient(),
            new InMemoryNextLinkRepository());
    new Thread(feedReader::read).start();
    Thread.sleep(300L);

    feedReader.stop();

    Thread.sleep(200L);
    int countAfterStop = count.get();
    Thread.sleep(300L);
    assertEquals(countAfterStop, count.get());
  }

  private static class DummyFeedReaderRestClient implements FeedReaderRestClient {

    @Override
    public List<FeedItem> getFeedItems(String feedUrl) {
      try {
        Thread.sleep(100L);
      } catch (InterruptedException ignored) {
      }
      List<FeedItem> feedItems = new java.util.ArrayList<>();
      FeedItem feedItem = new FeedItem();
      feedItem.setNext("/events?offset=100");
      feedItems.add(feedItem);
      return feedItems;
    }
  }
}
