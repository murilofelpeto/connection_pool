package com.felpeto.pool;

import java.io.UnsupportedEncodingException;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ConnectionPool {

  private static final String ONECLICK_HOST = "localhost";
  private static final String ONECLICK_URL = "http://localhost:8080/v1/app/xyz";


  public static void main(String[] args) throws Exception {
    final var connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(5);
    connectionManager.setDefaultMaxPerRoute(4);
    final var host = new HttpHost(ONECLICK_HOST, 8080);
    connectionManager.setMaxPerRoute(new HttpRoute(host), 5);

    final var post = createPost();

    final var connManager = new PoolingHttpClientConnectionManager();
    final var client = HttpClients.custom()
        .setConnectionManager(connManager)
        .build();

    for(int i = 0; i < 10; i++) {
      final var thread = new HttpClientThread(client, post);
      thread.start();
      thread.join();
    }
  }

  private static HttpPost createPost() throws UnsupportedEncodingException {
    final var post = new HttpPost(ONECLICK_URL);
    String json = "{\"action\":1,\"categoryId\":7,\"msisdn\":\"56920120660\"}";
    StringEntity entity = new StringEntity(json);
    post.setEntity(entity);
    post.setHeader("Accept", "application/json");
    post.setHeader("Content-type", "application/json");
    return post;
  }
}
