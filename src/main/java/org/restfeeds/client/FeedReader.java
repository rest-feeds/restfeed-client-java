package org.restfeeds.client;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedReader {

  private static final Logger log = LoggerFactory.getLogger(FeedReader.class);

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
    this.feedBaseUrl = feedBaseUrl;
    this.consumer = consumer;
    this.feedReaderRestClient = feedReaderRestClient;
    this.nextLinkRepository = nextLinkRepository;
  }

  public void read() {

    while (!stopped) {
      String link = nextLinkRepository.get(feedBaseUrl).orElse(feedBaseUrl);
      try {
        log.info("Read {}", link);
        List<FeedItem> items = feedReaderRestClient.getFeedItems(link);
        for (FeedItem feedItem : items) {
          log.debug("Consuming feed item {}", feedItem.getId());
          consumer.accept(feedItem);
          saveLink(feedBaseUrl, feedItem.getNext());
        }
      } catch (Exception e) {
        log.warn("Exception reading feed {}", link, e);
        delayNextRetry();
      }
    }
  }

  public void stop() {
    log.info("Stop feed reading");
    this.stopped = true;
  }

  public void setDelayRetry(Duration delayRetry) {
    this.delayRetry = delayRetry;
  }

  protected void saveLink(String feed, String nextLink) {
    String link = nextLink;
    if (!URI.create(nextLink).isAbsolute()) {
      link = URI.create(feed).resolve(nextLink).toString();
    }
    log.debug("Saving next link {}", link);
    nextLinkRepository.save(feed, link);
  }

  protected void delayNextRetry() {
    try {
      Thread.sleep(delayRetry.toMillis());
    } catch (InterruptedException ignored) {
    }
  }
}
