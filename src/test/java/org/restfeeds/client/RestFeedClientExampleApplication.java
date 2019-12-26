package org.restfeeds.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestFeedClientExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestFeedClientExampleApplication.class, args);
  }

  @Bean
  FeedItemConsumer feedItemConsumer() {
    return System.out::println;
  }
}
