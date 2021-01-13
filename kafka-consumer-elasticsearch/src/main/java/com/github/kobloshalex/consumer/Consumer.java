package com.github.kobloshalex.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class Consumer {
  private Consumer() {}

  public static final String BOOTSTRAP_SERVER = "127.0.0.1:9092";
  public static final String GROUP_ID = "kafka-demo-elasticsearch";

  public static KafkaConsumer<String, String> createConsumer(String topic) {
    Properties properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
    properties.setProperty(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
    consumer.subscribe(Collections.singletonList(topic));

    return consumer;
  }
}
