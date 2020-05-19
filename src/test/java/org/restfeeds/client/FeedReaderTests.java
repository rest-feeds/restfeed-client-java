package org.restfeeds.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class FeedReaderTests {

  private static final Logger logger = Logger.getLogger(FeedReaderTests.class.getName());

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

  @Test
  void shouldNotRead() throws Exception {
    AtomicInteger count = new AtomicInteger(0);
    FeedReader feedReader = new FeedReader(
        "http://localhost/events",
        feedItem -> count.incrementAndGet(),
        new DummyFeedReaderRestClient(),
        new InMemoryNextLinkRepository()) {

      @Override
      protected boolean shouldRead() {
        return false;
      }

    };

    new Thread(feedReader::read).start();
    Thread.sleep(300L);
    assertEquals(0, count.get());
  }

  @Test
  void shouldInvokeHooks() throws Exception {
    AtomicInteger count = new AtomicInteger(0);
    AtomicInteger onBeforeReadCount = new AtomicInteger(0);
    AtomicInteger onAfterReadCount = new AtomicInteger(0);
    AtomicInteger onBeforeStopCount = new AtomicInteger(0);
    AtomicInteger onAfterStopCount = new AtomicInteger(0);
    FeedReader feedReader = new FeedReader(
        "http://localhost/events",
        feedItem -> count.incrementAndGet(),
        new DummyFeedReaderRestClient(),
        new InMemoryNextLinkRepository()) {

      private boolean shouldRead = true;

      @Override
      protected boolean shouldRead() {
        logger.log(Level.INFO, "should read? {0}", shouldRead);
        return shouldRead;
      }

      @Override
      protected void onBeforeRead() {
        int i = onBeforeReadCount.incrementAndGet();
        logger.log(Level.INFO, "onBeforeRead called {0} time(s)", i);
      }

      @Override
      protected void onAfterRead() {
        int i = onAfterReadCount.incrementAndGet();
        logger.log(Level.INFO, "onAfterRead called {0} time(s)", i);
        if (1 == onBeforeReadCount.get()) {
          shouldRead = false;
        }
      }

      @Override
      protected void onBeforeStop() {
        int i = onBeforeStopCount.incrementAndGet();
        logger.log(Level.INFO, "onBeforeStop called {0} time(s)", i);
      }

      @Override
      protected void onAfterStop() {
        if (1 == onBeforeStopCount.get()) {
          int i = onAfterStopCount.incrementAndGet();
          logger.log(Level.INFO, "onAfterStop called {0} time(s) after onBeforeStop", i);
        }
      }
    };

    new Thread(feedReader::read).start();
    Thread.sleep(300L);

    assertEquals(1, count.get(), "Consumer should be called 1 time.");
    assertEquals(1, onBeforeReadCount.get(), "onBeforeRead should be called 1 time.");
    assertEquals(1, onAfterReadCount.get(), "onAfterRead should be called 1 time.");
    assertEquals(0, onBeforeStopCount.get(), "onBeforeStop should not be called before stopping.");
    assertEquals(0, onAfterStopCount.get(), "onAfterStop should not be called before stopping.");

    feedReader.stop();

    assertEquals(1, onBeforeStopCount.get(), "onBeforeStop should be called 1 time.");

    Thread.sleep(300L);

    assertEquals(1, count.get(), "Consumer should be called 1 time.");
    assertEquals(1, onBeforeReadCount.get(), "onBeforeRead should be called 1 time.");
    assertEquals(1, onAfterReadCount.get(), "onAfterRead should be called 1 time.");
    assertEquals(1, onBeforeStopCount.get(), "onBeforeStop should be called 1 time.");
    assertEquals(1, onAfterStopCount.get(), "onAfterStop should be called 1 time.");

  }

  @Test
  void shouldNotAcceptMoreThanOnce() throws Exception {
    AtomicInteger count = new AtomicInteger(0);
    FeedReader feedReader = new FeedReader(
        "http://localhost/events",
        feedItem -> count.incrementAndGet(),
        new DummyFeedReaderRestClient(),
        new InMemoryNextLinkRepository()) {

      @Override
      protected boolean shouldAccept(String link, List<FeedItem> feedItem) {
        return count.get() < 1;
      }

    };

    new Thread(feedReader::read).start();
    Thread.sleep(300L);
    assertEquals(1, count.get());
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
