## gRPC example
Created simple gRPC bidirection streaming API with mocked stock order submission.

### gRPC definition
Model and API definition for the streaming stock order submission located at 
**StockOrderSubmission.proto** under **protobuf-model** module.

To build the java classes for the gRPC and protobuf, just run ```maven compile``` or maven goals above compile.
The **protobuf-maven-plugin** is used to generate the codes.

### gRPC server
Located at **service** module, by running the **StockOrderSubmissionServerMain** will start server on port 50051.

That listens for streaming input of stock orders.
  
### gRPC client
Located at **client** module, by running the **StockOrderSubmissionClientMain** will start client on port 50051.

That sends in stock orders to the server side by streaming bulk of orders. 
Each bulk of orders is considered as its own stream, thus mark as stream completed when the bulk is finished.


### Reference
[grpc-java](https://github.com/grpc/grpc-java)   