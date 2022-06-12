package no.kaicao.service.stock;

import io.grpc.stub.StreamObserver;
import no.kaicao.grpc.stock.internal.StockOrderRequest;
import no.kaicao.grpc.stock.internal.StockOrderResponse;
import no.kaicao.grpc.stock.internal.StockOrderSubmissionServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.UUID;

public class StockOrderSubmissionService extends StockOrderSubmissionServiceGrpc.StockOrderSubmissionServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(StockOrderSubmissionService.class);
  private static final Clock CLOCK = Clock.systemUTC();

  @Override
  public StreamObserver<StockOrderRequest> submit(StreamObserver<StockOrderResponse> responseObserver) {
    return new StreamObserver<StockOrderRequest>() {
      @Override
      public void onNext(StockOrderRequest stockOrderRequest) {
        if (stockOrderRequest == null) {
          return;
        }

        LOG.info("submit stream received " + stockOrderRequest.toString());
        StockOrderResponse response = StockOrderResponse.newBuilder()
            .setOrderUUID(UUID.randomUUID().toString())
            .setExecuteTimestamp(CLOCK.millis())
            .setStock(stockOrderRequest.getStock())
            .setPrice(stockOrderRequest.getPrice() + 0.1)
            .build();
        responseObserver.onNext(response);
      }

      @Override
      public void onError(Throwable throwable) {
        LOG.error("Error occured: " + throwable.getMessage(), throwable);
      }

      @Override
      public void onCompleted() {
        LOG.info("submit stream completed");
        responseObserver.onCompleted();
      }
    };
  }
}
