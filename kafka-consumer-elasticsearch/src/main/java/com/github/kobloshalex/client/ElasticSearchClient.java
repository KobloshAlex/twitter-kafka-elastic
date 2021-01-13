package com.github.kobloshalex.client;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchClient {

  public static RestHighLevelClient createClient() {
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    credentialsProvider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(
            ElasticSearchKeys.USERNAME.getValue(), ElasticSearchKeys.PASSWORD.getValue()));

    final RestClientBuilder clientBuilder =
        RestClient.builder(
                new HttpHost(
                    ElasticSearchKeys.HOST_NAME.getValue(),
                    Integer.parseInt(ElasticSearchKeys.PORT.getValue()),
                    ElasticSearchKeys.PROTOCOL.getValue()))
            .setHttpClientConfigCallback(
                httpAsyncClientBuilder ->
                    httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

    return new RestHighLevelClient(clientBuilder);
  }
}
