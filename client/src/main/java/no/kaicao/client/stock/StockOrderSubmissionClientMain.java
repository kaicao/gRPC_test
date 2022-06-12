package no.kaicao.client.stock;

import com.oracle.tools.packager.Log;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import no.kaicao.grpc.stock.internal.*;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StockOrderSubmissionClientMain {

  private static final Clock CLOCK = Clock.systemUTC();

  public static void main(String[] args) throws InterruptedException {
    // Access a service running on the local machine on port 50051
    String target = "localhost:50051";

    // Create a communication channel to the server, known as a Channel. Channels are thread-safe
    // and reusable. It is common to create channels at the beginning of your application and reuse
    // them until the application shuts down.
    ManagedChannel channel =
        ManagedChannelBuilder.forTarget(target)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();
    try {
      StockOrderSubmissionClient client = new StockOrderSubmissionClient(channel);

      Log.info("Submit first bulk of orders");
      client.submit(createTestOrders1());
      TimeUnit.SECONDS.sleep(5);  // wait in between bulk

      Log.info("Submit second bulk of orders");
      client.submit(createTestOrders2());
      TimeUnit.SECONDS.sleep(5);  // wait in between bulk

      Log.info("Submit third bulk of orders");
      client.submit(createTestOrders3());

      // Give some time to execute before shutdown
      TimeUnit.SECONDS.sleep(20L);
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
      // resources the channel should be shut down when it will no longer be used. If it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  private static List<StockOrderRequest> createTestOrders1() {
    List<StockOrderRequest> orders = new ArrayList<>();
    orders.add(createRequest("BRK.A", 439780.0, true));
    orders.add(createRequest("BRK.A", 439776.0, false));
    orders.add(createRequest("BRK.A", 439790.0, true));
    return orders;
  }

  private static List<StockOrderRequest> createTestOrders2() {
    List<StockOrderRequest> orders = new ArrayList<>();
    orders.add(createRequest("SAS", 0.65, true));
    orders.add(createRequest("SAS", 0.7, false));
    orders.add(createRequest("SAS", 0.8, true));
    return orders;
  }

  private static List<StockOrderRequest> createTestOrders3() {
    List<StockOrderRequest> orders = new ArrayList<>();
    orders.add(createRequest("MSFT", 252.99, true));
    orders.add(createRequest("MSFT", 253.50, false));
    orders.add(createRequest("MSFT", 254.82, true));
    return orders;
  }

  private static StockOrderRequest createRequest(String stockSymbol, double price, boolean buy) {
    return StockOrderRequest.newBuilder()
        .setAccount(
            Account.newBuilder()
                .setAccountNumber("IBAN293847")
                .setOwnerName("Kai")
                .setBankName("SomeBank")
                .build())
        .setStock(
            Stock.newBuilder()
                .setName(stockSymbol + " name")
                .setSymbol(stockSymbol)
                .setStockClass("A")
                .build())
        .setOrderType(buy ? StockOrderType.BUY : StockOrderType.SELL)
        .addFlags(StockOrderFlag.PENDING)
        .setPrice(price)
        .setIssueTimestamp(CLOCK.millis())
        .putProperties("prop1", "value1")
        .putProperties("prop2", "value2")
        .putProperties("prop3", "value3")
        .build();
  }
}
