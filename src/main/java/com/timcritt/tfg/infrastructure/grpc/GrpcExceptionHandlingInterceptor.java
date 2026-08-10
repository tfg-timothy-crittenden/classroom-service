package com.timcritt.tfg.infrastructure.grpc;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.springframework.stereotype.Component;

@Component
public class GrpcExceptionHandlingInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> io.grpc.ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        try {
            return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
            }, headers);
        } catch (RuntimeException ex) {
            call.close(Status.INTERNAL.withDescription("Unexpected gRPC server error").withCause(ex), new Metadata());
            return new ServerCall.Listener<>() {
            };
        }
    }
}

