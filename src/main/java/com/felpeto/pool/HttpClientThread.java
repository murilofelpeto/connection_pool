package com.felpeto.pool;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

@Slf4j
public class HttpClientThread extends Thread {

  private final CloseableHttpClient client;
  private final HttpPost post;

  private PoolingHttpClientConnectionManager connManager;
  private int leasedConn;

  HttpClientThread(final CloseableHttpClient client, final HttpPost post,
      final PoolingHttpClientConnectionManager connManager) {
    this.client = client;
    this.post = post;
    this.connManager = connManager;
    leasedConn = 0;
  }

  HttpClientThread(final CloseableHttpClient client, final HttpPost post) {
    this.client = client;
    this.post = post;
  }

  final int getLeasedConn() {
    return leasedConn;
  }

  @Override
  public final void run() {
    try {
      log.debug("Thread Running: " + getName());

      log.debug("Thread Running: " + getName());

      if (connManager != null) {
        log.info("Before - Leased Connections = " + connManager.getTotalStats().getLeased());
        log.info("Before - Available Connections = " + connManager.getTotalStats().getAvailable());
      }

      final HttpResponse response = client.execute(post);

      if (connManager != null) {
        leasedConn = connManager.getTotalStats().getLeased();
        log.info("After - Leased Connections = " + connManager.getTotalStats().getLeased());
        log.info("After - Available Connections = " + connManager.getTotalStats().getAvailable());
      }

      final var httpEntity = response.getEntity();
      final var apiOutput = EntityUtils.toString(httpEntity);

      log.info("Thread: {} - API response {}", getName(), apiOutput);

      EntityUtils.consume(response.getEntity());

    } catch (final IOException ex) {
      log.error("", ex);
    }
  }
}
