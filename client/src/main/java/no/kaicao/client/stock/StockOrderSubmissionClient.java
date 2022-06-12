package no.kaicao.client.stock;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import no.kaicao.grpc.stock.internal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StockOrderSubmissionClient {

  private static final Logger LOG = LoggerFactory.getLogger(StockOrderSubmissionClient.class);

  private final StockOrderSubmissionServiceGrpc.StockOrderSubmissionServiceStub serviceStub;

  // construct client for accessing server using the existing channel
  public StockOrderSubmissionClient(Channel channel) {
    this.serviceStub = StockOrderSubmissionServiceGrpc.newStub(channel);
  }

  public void submit(List<StockOrderRequest> orders) {
    if (orders == null || orders.size() == 0) {
      return;
    }

    StreamObserver<StockOrderRequest> stream = serviceStub.submit(
        new StreamObserver<StockOrderResponse>() {
          @Override
          public void onNext(StockOrderResponse stockOrderResponse) {
            LOG.info("Received response: " + stockOrderResponse.toString());
          }

          @Override
          public void onError(Throwable throwable) {
            LOG.error("onError occurred", throwable);
          }

          @Override
          public void onCompleted() {
            LOG.info("onCompleted");
          }
        });

    try {
      LOG.info("Start submitting {} orders", orders.size());
      for (StockOrderRequest order : orders) {
        stream.onNext(order);
      }
      LOG.info("Finished submitting {} orders", orders.size());
      stream.onCompleted();
    } catch (Exception e) {
      LOG.error("Submission of order failed", e);
      stream.onError(e);
    }
  }

}
