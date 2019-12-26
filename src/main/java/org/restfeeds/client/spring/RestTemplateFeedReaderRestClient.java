package org.restfeeds.client.spring;

import java.util.Arrays;
import java.util.List;
import org.restfeeds.client.FeedItem;
import org.restfeeds.client.FeedReaderRestClient;
import org.springframework.web.client.RestTemplate;

public class RestTemplateFeedReaderRestClient implements FeedReaderRestClient {

  private final RestTemplate restTemplate;

  public RestTemplateFeedReaderRestClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<FeedItem> getFeedItems(String feedUrl) {

    FeedItem[] items = restTemplate.getForObject(feedUrl, FeedItem[].class);

    if (items == null) {
      return null;
    }

    return Arrays.asList(items);
  }
}
