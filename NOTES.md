
## gRPC overview
https://docs.microsoft.com/en-us/aspnet/core/grpc/?view=aspnetcore-6.0

gRPC is a language agnostic, high-performance Remote Procedure Call (RPC) framework.

The main benefits of gRPC are:
- Modern, high-performance, lightweight RPC framework.
- Contract-first API development, using Protocol Buffers by default, allowing for language agnostic implementations.
- Tooling available for many languages to generate strongly-typed servers and clients.
- Supports client, server, and bi-directional streaming calls.
- Reduced network usage with Protobuf binary serialization.

These benefits make gRPC ideal for:
- Lightweight microservices where efficiency is critical.
- Polyglot systems where multiple languages are required for development.
- Point-to-point real-time services that need to handle streaming requests or responses.

## Performance best practice
https://grpc.io/docs/guides/performance/

- Use streaming RPCs when handling a long-lived logical flow of data from the client-to-server, server-to-client, or in both directions. Streams can avoid continuous RPC initiation, which includes connection load balancing at the client-side, starting a new HTTP/2 request at the transport layer, and invoking a user-defined method handler on the server side.

    Streams, however, cannot be load balanced once they have started and can be hard to debug for stream failures. They also might increase performance at a small scale but can reduce scalability due to load balancing and complexity, so they should only be used when they provide substantial performance or simplicity benefit to application logic. Use streams to optimize the application, not gRPC.


- Each gRPC channel uses 0 or more HTTP/2 connections and each connection usually has a limit on the number of concurrent streams. When the number of active RPCs on the connection reaches this limit, additional RPCs are queued in the client and must wait for active RPCs to finish before they are sent. Applications with high load or long-lived streaming RPCs might see performance issues because of this queueing


### Java
- Use non-blocking stubs to parallelize RPCs.

- Provide a custom executor that limits the number of threads, based on your workload (cached (default), fixed, forkjoin, etc).

### GPB Versioning
https://techhub.hpe.com/eginfolib/networking/docs/sdn/sdnc2_7/5200-0910prog/content/s_sdnc-app-ha-versioning-GPB.html#:~:text=Versioning%20is%20controlled%20in%20the,message%20to%20be%20considered%20valid.
GPB versioning rules
A message version is a function of the field numbering and tags provided by GPB and how those are changed between different iterations of the data structure. The following are general rules about how .proto fields should be updated to insure compatible GPB versioned data:
Do not change the numeric tags for any existing (previous version) fields.

New fields should be tagged OPTIONAL/REPEATED (never REQUIRED). New fields should also be assigned a new, unique field ID.

Removal of OPTIONAL/REPEATED tagged fields are allowed and will not affect compatibility.

Changing a default value for a field is allowed. (Default values are sent only if the field is not provided.)

There are specific rules for changing the field types. Some type conversions are compatible while others are not (see GPB documentation for specific details).

Note: It is generally advised that the minimal number of fields be marked with a REQUIRED tag as these fields become fixed in the schema and will always have to be present in future versions of the message.

### gRPC versioning
https://docs.microsoft.com/en-us/aspnet/core/grpc/versioning?view=aspnetcore-6.0

#### Non-breaking changes
These changes are non-breaking at a gRPC protocol level and .NET binary level.

* Adding a new service
* Adding a new method to a service
* Adding a field to a request message - Fields added to a request message are deserialized with the default value on the server when not set. To be a non-breaking change, the service must succeed when the new field isn't set by older clients.
* Adding a field to a response message - Fields added to a response message are deserialized into the message's unknown fields collection on the client.
* Adding a value to an enum - Enums are serialized as a numeric value. New enum values are deserialized on the client to the enum value without an enum name. To be a non-breaking change, older clients must run correctly when receiving the new enum value.

