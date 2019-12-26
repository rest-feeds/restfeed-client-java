package org.restfeeds.client;

import java.util.Optional;

public interface NextLinkRepository {

  void save(String feed, String nextLink);

  Optional<String> get(String feed);
}
