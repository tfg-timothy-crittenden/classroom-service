package com.timcritt.tfg.infrastructure.grpc;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GrpcInternalApiKeyInterceptor implements ServerInterceptor {

    /**
     * gRPC metadata key for the internal API key.
     * Header name is "X-Internal-Api-Key"; gRPC normalises all metadata keys to lowercase,
     * so the wire key is "x-internal-api-key". Clients must attach this key to every call.
     */
    public static final String HEADER_NAME = "X-Internal-Api-Key";
    public static final Metadata.Key<String> API_KEY_METADATA_KEY =
            Metadata.Key.of(HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER);

    private final String internalApiKey;

    public GrpcInternalApiKeyInterceptor(@Value("${security.internal.api-key}") String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }

    @Override
    public <ReqT, RespT> io.grpc.ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        String providedKey = headers.get(API_KEY_METADATA_KEY);
        if (internalApiKey == null || internalApiKey.isBlank() || providedKey == null || !providedKey.equals(internalApiKey)) {
            log.warn("gRPC internal auth rejected: method={}, keyProvided={}",
                    call.getMethodDescriptor().getFullMethodName(), providedKey != null);
            call.close(Status.UNAUTHENTICATED.withDescription("Missing or invalid internal API key"), new Metadata());
            return new ServerCall.Listener<>() {
            };
        }
        return next.startCall(call, headers);
    }
}

