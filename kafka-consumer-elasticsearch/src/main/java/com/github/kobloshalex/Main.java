package com.github.kobloshalex;

import com.github.kobloshalex.client.ElasticSearchClient;
import com.github.kobloshalex.consumer.Consumer;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
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

  public static void main(String[] args) throws IOException {
    try (RestHighLevelClient client = ElasticSearchClient.createClient()) {

      KafkaConsumer<String, String> consumer = Consumer.createConsumer("twitter_tweets");

      while (true) {
        ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));

        Integer recordCount = consumerRecords.count();
        logger.info("Received records {}", recordCount);

        BulkRequest bulkRequest = new BulkRequest();

        for (ConsumerRecord<String, String> record : consumerRecords) {
          final String id = extractIdFromTweeterPayload(record.value());
          IndexRequest indexRequest =
              new IndexRequest("twitter", "tweets", id).source(record.value(), XContentType.JSON);

          bulkRequest.add(indexRequest);
        }
        if (recordCount > 0) {
          client.bulk(bulkRequest, RequestOptions.DEFAULT);
          logger.info("Commit sync...");
          consumer.commitSync();
        }
      }
    }
  }

  private static String extractIdFromTweeterPayload(String tweeterPayload) {

    return new JsonParser().parse(tweeterPayload).getAsJsonObject().get("id_str").getAsString();
  }
}
