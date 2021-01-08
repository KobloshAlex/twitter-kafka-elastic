package com.github.kobloshalex.producer;

import com.github.kobloshalex.keys.TwitterKeys;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {

  private static final Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());

  List<String> terms = Lists.newArrayList("bitcoin");

  public void run() {
    final BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(1000);

    final Client client = createTwitterClint(msgQueue);

    client.connect();

    try (KafkaProducer<String, String> producer = new Producer().createKafkaProducer()) {

      Runtime.getRuntime()
          .addShutdownHook(
              new Thread(
                  () -> {
                    logger.info("Stopping application");
                    logger.info("Close client");
                    client.stop();
                    logger.info("Close producer");
                    producer.close();
                  }));

      while (!client.isDone()) {
        String msg = null;
        try {
          msg = msgQueue.poll(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
          client.stop();
        }
        if (msg != null) {
          final String[] array = msg.split(",");
          final String message = array[0] + " - " + array[3];
          logger.info(message);
          producer.send(
              new ProducerRecord<>("twitter_tweets", null, message),
              (recordMetadata, e) -> {
                if (e != null) {
                  logger.error(e.toString());
                }
              });
        }
      }
      logger.info("End of Application");
    }
  }

  private Client createTwitterClint(BlockingQueue<String> msgQueue) {

    final Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
    final StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

    hosebirdEndpoint.trackTerms(terms);

    final Authentication hosebirdAuth =
        new OAuth1(
            TwitterKeys.API_KEY.getValue(),
            TwitterKeys.API_SECRET_KEY.getValue(),
            TwitterKeys.TOKEN.getValue(),
            TwitterKeys.SECRET.getValue());

    final ClientBuilder builder =
        new ClientBuilder()
            .name("Hosebird-Client-012")
            .hosts(hosebirdHosts)
            .authentication(hosebirdAuth)
            .endpoint(hosebirdEndpoint)
            .processor(new StringDelimitedProcessor(msgQueue));

    return builder.build();
  }
}
