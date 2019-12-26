package org.restfeeds.client;

import java.util.List;

public interface FeedReaderRestClient {

  List<FeedItem> getFeedItems(String feedUrl);
}
