package no.kaicao.service.stock;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class StockOrderSubmissionServer {
  private static final Logger LOG = LoggerFactory.getLogger(StockOrderSubmissionServer.class);

  private Server server;

  public void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = ServerBuilder.forPort(port)
        .addService(new StockOrderSubmissionService())
        .build()
        .start();
    LOG.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      LOG.info("*** shutting down gRPC server since JVM is shutting down");
      try {
        StockOrderSubmissionServer.this.stop();
      } catch (InterruptedException e) {
        LOG.error("Server stop is interrupted", e);
      }
      LOG.info("*** server shut down");
    }));
  }

  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }
}
