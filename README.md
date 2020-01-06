# restfeed-client-java

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.restfeeds/restfeed-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.restfeeds/restfeed-client)

Client library to consume [REST Feeds](http://rest-feeds.org/).

Written in pure Java without transitive dependencies.

An included Spring Boot Auto Configuration supports the implementation as a Spring Boot application.


## Getting Started 

Go to [start.spring.io](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.2.2.RELEASE&packaging=jar&jvmVersion=1.8&groupId=com.example&artifactId=restfeed-server-example&name=restfeed-server-example&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.restfeed-server-example&dependencies=web,jdbc,h2) and create an new application. 
Select this dependency:

- Spring Web (for RestTemplate and HttpMessageConverters; we do not need the web server)

Then add this library to your `pom.xml`:

```xml
    <dependency>
      <groupId>org.restfeeds</groupId>
      <artifactId>restfeed-client</artifactId>
      <version>0.0.1</version>
    </dependency>
```

Add the base URL of the feed endpoint to your `application.properties`:

```properties
restfeed.client.url=https://example.rest-feeds.org/movies
```

And implement a [`FeedItemConsumer`](src/main/java/org/restfeeds/client/FeedItemConsumer.java):

```java
@Component
public class SimpleFeedItemConsumer implements FeedItemConsumer{

  @Override
  public void accept(FeedItem feedItem) {
    System.out.println(feedItem);
  }
}
```

And run the application.


## Components

### [FeedReader](src/main/java/org/restfeeds/client/FeedReader.java)

The [`FeedReader`](src/main/java/org/restfeeds/client/FeedReader.java) is the core class that polls the feed endpoint for new items in an endless loop.

Call the `read()` method to start reading the feed.

When shutting down the application, call the `stop()` method to end the endless loop.

The Spring [RestFeedClientAutoConfiguration](src/main/java/org/restfeeds/client/spring/RestFeedClientAutoConfiguration.java) starts and stops the FeedReader on application startup and shutdown.


### [FeedItemConsumer](src/main/java/org/restfeeds/client/FeedItemConsumer.java)

Implement [FeedItemConsumer](src/main/java/org/restfeeds/client/FeedItemConsumer.java) interface to handle feed items.


### [NextLinkRepository](src/main/java/org/restfeeds/client/NextLinkRepository.java)

Provide an implementation how the next link is stored.
The `save()` method is called directly after a feed item was consumed.

Typically, the NextLinkRepository is implemented as a SQL or NoSQL database.

An [`InMemoryNextLinkRepository`](src/main/java/org/restfeeds/client/InMemoryNextLinkRepository.java) is provided for testing.

The Spring [RestFeedClientAutoConfiguration](src/main/java/org/restfeeds/client/spring/RestFeedClientAutoConfiguration.java) configures an `InMemoryNextLinkRepository`, when no other `NextLinkRepository` bean was created.


### [FeedReaderRestClient](src/main/java/org/restfeeds/client/FeedReaderRestClient.java)

Implement this interface to perform the HTTP connection to the feed endpoint, authenticate, negotiate the content type, and do the unmarshalling.

The Spring [RestFeedClientAutoConfiguration](src/main/java/org/restfeeds/client/spring/RestFeedClientAutoConfiguration.java) configures a [`RestTemplateFeedReaderRestClient`](src/main/java/org/restfeeds/client/spring/RestTemplateFeedReaderRestClient.java), when no other `FeedReaderRestClient` bean was created.
Consider configuring the [`RestTemplateBuilder`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-resttemplate) for your needs.


## Spring Properties

[RestFeedClientAutoConfiguration](src/main/java/org/restfeeds/client/spring/RestFeedClientAutoConfiguration.java) uses these properties:

| Key | Default Value | Description
| --- | --- | --- 
| `restfeed.client.enabled`  | `true` | Enable REST feed client auto configuration and run FeedReader on application start.
| `restfeed.client.url`      |        | The base URL of the feed endpoint to consume. Required.
| `restfeed.client.username` |        | The username for basic authentication. Optional.
| `restfeed.client.password` |        | The password for basic authentication. Optional.


## FAQ

### How to disable the embedded web server

Spring Web automatically starts up a Tomcat server on port 8080.

Set this property to disable:

```properties
spring.main.web-application-type=none
```

