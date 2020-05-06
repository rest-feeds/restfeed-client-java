package org.restfeeds.client;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class FeedReader {

  private static final Logger logger = Logger.getLogger(FeedReader.class.getName());

  private final String feedBaseUrl;
  private final FeedItemConsumer consumer;
  private final FeedReaderRestClient feedReaderRestClient;
  private final NextLinkRepository nextLinkRepository;

  private Duration delayRetry = Duration.of(5, SECONDS);
  private boolean stopped;

  public FeedReader(
      String feedBaseUrl,
      FeedItemConsumer consumer,
      FeedReaderRestClient feedReaderRestClient,
      NextLinkRepository nextLinkRepository) {
    Objects.requireNonNull(feedBaseUrl, "feed url is not set");
    this.feedBaseUrl = feedBaseUrl;
    this.consumer = consumer;
    this.feedReaderRestClient = feedReaderRestClient;
    this.nextLinkRepository = nextLinkRepository;
  }

  public void read() {

    while (!stopped) {

      if (shouldRead()) {

        String link = nextLinkRepository.get(feedBaseUrl).orElse(feedBaseUrl);
        try {
          onBeforeRead();
          logger.log(INFO, "Reading {0}", link);
          List<FeedItem> items = feedReaderRestClient.getFeedItems(link);
          for (FeedItem feedItem : items) {
            logger.log(FINE, "Consuming feed item {0}", feedItem.getId());
            consumer.accept(feedItem);
            saveLink(feedBaseUrl, feedItem.getNext());
          }
        } catch (Exception e) {
          logger.log(WARNING, "Exception reading feed " + link, e);
          delayNextRetry();
        } finally {
          onAfterRead();
        }
      }

    }
    onAfterStop();
  }

  public void stop() {
    onBeforeStop();
    logger.info("Stop feed reading");
    this.stopped = true;
  }

  protected boolean shouldRead() {
    return true;
  }

  protected void onBeforeRead() {
    // hook for subclasses
  }

  protected void onAfterRead() {
    // hook for subclasses
  }

  protected void onBeforeStop() {
    // hook for subclasses
  }

  protected void onAfterStop() {
    // hook for subclasses
  }

  public void setDelayRetry(Duration delayRetry) {
    this.delayRetry = delayRetry;
  }

  protected void saveLink(String feed, String nextLink) {
    String link = nextLink;
    if (!URI.create(nextLink).isAbsolute()) {
      link = URI.create(feed).resolve(nextLink).toString();
    }
    logger.log(FINE, "Saving next link {0}", link);
    nextLinkRepository.save(feed, link);
  }

  protected void delayNextRetry() {
    try {
      Thread.sleep(delayRetry.toMillis());
    } catch (InterruptedException ignored) {
    }
  }
}
