package com.github.kobloshalex.client;

public enum ElasticSearchKeys {
  //https://:@
  HOST_NAME("kafka-elastic-2711506839.us-east-1.bonsaisearch.net"),
  USERNAME("a71lo9ybpx"),
  PROTOCOL("https"),
  PORT("443"),
  PASSWORD("n4klgh68v4");

  private final String value;

  ElasticSearchKeys(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
