package no.kaicao.service.stock;

import java.io.IOException;

public class StockOrderSubmissionServerMain {

  public static void main(String[] args) throws IOException, InterruptedException {
    StockOrderSubmissionServer server = new StockOrderSubmissionServer();
    server.start();
    server.blockUntilShutdown();
  }
}
