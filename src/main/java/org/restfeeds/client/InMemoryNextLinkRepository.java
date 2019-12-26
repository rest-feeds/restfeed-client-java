package org.restfeeds.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryNextLinkRepository implements NextLinkRepository {

  Map<String, String> feedNextLink = new HashMap<>();

  @Override
  public void save(String feed, String nextLink) {
    feedNextLink.put(feed, nextLink);
  }

  @Override
  public Optional<String> get(String feed) {
    return Optional.ofNullable(feedNextLink.get(feed));
  }
}
