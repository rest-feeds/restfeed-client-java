package org.restfeeds.client.spring;

import org.restfeeds.client.FeedItemConsumer;
import org.restfeeds.client.FeedReader;
import org.restfeeds.client.FeedReaderRestClient;
import org.restfeeds.client.InMemoryNextLinkRepository;
import org.restfeeds.client.NextLinkRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(FeedReader.class)
@ConditionalOnProperty(
    value = "restfeed.client.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class RestFeedClientAutoConfiguration {

  final RestFeedClientConfigurationProperties properties;

  public RestFeedClientAutoConfiguration(RestFeedClientConfigurationProperties properties) {
    this.properties = properties;
  }

  @Bean
  public CommandLineRunner feedReaderRunner(FeedReader feedReader) {
    return args -> feedReader.read();
  }

  @Bean(destroyMethod = "stop")
  @ConditionalOnMissingBean(FeedReader.class)
  FeedReader feedReader(
      FeedItemConsumer feedItemConsumer,
      FeedReaderRestClient feedReaderRestClient,
      NextLinkRepository nextLinkRepository) {
    return new FeedReader(
        properties.getUrl(), feedItemConsumer, feedReaderRestClient, nextLinkRepository);
  }

  @Bean
  @ConditionalOnClass(RestTemplateBuilder.class)
  @ConditionalOnMissingBean(FeedReaderRestClient.class)
  FeedReaderRestClient restTemplateFeedReaderClient(RestTemplateBuilder restTemplateBuilder) {
    if (properties.getUsername() != null && properties.getPassword() != null) {
      restTemplateBuilder =
          restTemplateBuilder.basicAuthentication(
              properties.getUsername(), properties.getPassword());
    }
    return new RestTemplateFeedReaderRestClient(restTemplateBuilder.build());
  }

  @Bean
  @ConditionalOnMissingBean(NextLinkRepository.class)
  InMemoryNextLinkRepository inMemoryNextLinkRepository() {
    return new InMemoryNextLinkRepository();
  }
}
