# restfeed-client-java

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.restfeeds/restfeed-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.restfeeds/restfeed-client)

Core client library to consume [REST Feeds](http://rest-feeds.org/).

Written in pure Java without transitive dependencies.

## Spring Boot

Use this library when using Spring Boot:

- [restfed-client-spring](https://github.com/rest-feeds/restfeed-client-spring)

## Getting Started

Follow the [Getting Started](https://github.com/rest-feeds/restfeed-client-spring) section in the Spring implementation.


## Components

### [FeedReader](src/main/java/org/restfeeds/client/FeedReader.java)

The [`FeedReader`](src/main/java/org/restfeeds/client/FeedReader.java) is the core class that polls the feed endpoint for new items in an endless loop.

Call the `read()` method to start reading the feed.

When shutting down the application, call the `stop()` method to end the endless loop.


### [FeedItemConsumer](src/main/java/org/restfeeds/client/FeedItemConsumer.java)

Implement [FeedItemConsumer](src/main/java/org/restfeeds/client/FeedItemConsumer.java) interface to handle feed items.


### [NextLinkRepository](src/main/java/org/restfeeds/client/NextLinkRepository.java)

Provide an implementation how the next link is stored.
The `save()` method is called directly after a feed item was consumed.

Typically, the NextLinkRepository is implemented as a SQL or NoSQL database.

An [`InMemoryNextLinkRepository`](src/main/java/org/restfeeds/client/InMemoryNextLinkRepository.java) is provided for testing.


### [FeedReaderRestClient](src/main/java/org/restfeeds/client/FeedReaderRestClient.java)

Implement this interface to perform the HTTP connection to the feed endpoint, authenticate, negotiate the content type, and do the unmarshalling.


