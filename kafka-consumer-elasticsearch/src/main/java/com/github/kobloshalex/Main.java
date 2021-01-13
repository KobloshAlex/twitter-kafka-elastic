package com.github.kobloshalex;

import com.github.kobloshalex.client.ElasticSearchClient;
import com.github.kobloshalex.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class Main {
  public static final Logger logger = LoggerFactory.getLogger(Main.class.getName());

  public static void main(String[] args) throws IOException, InterruptedException {
    try (RestHighLevelClient client = ElasticSearchClient.createClient()) {

      String json = "{ \"foo\": \"bar\" }";

      KafkaConsumer<String, String> consumer = Consumer.createConsumer("twitter_tweets");

      while (true) {
        ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String, String> record : consumerRecords) {
          System.out.println(record.value());
          IndexRequest indexRequest =
              new IndexRequest("twitter", "tweets").source(record.value(), XContentType.JSON);
          IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
          logger.info(indexResponse.getId());
          Thread.sleep(1000);
          logger.info("SENT");
        }
      }
    }
  }
}