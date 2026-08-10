package com.timcritt.tfg.infrastructure.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConditionalOnProperty(prefix = "classroom.grpc", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(GrpcServerProperties.class)
@RequiredArgsConstructor
@Slf4j
public class GrpcServerLifecycle {

    private final GrpcServerProperties properties;
    private final ClassroomAuthorizationGrpcService classroomAuthorizationGrpcService;
    private final GrpcInternalApiKeyInterceptor internalApiKeyInterceptor;
    private final GrpcExceptionHandlingInterceptor exceptionHandlingInterceptor;

    private Server server;

    @PostConstruct
    void start() throws IOException {
        server = ServerBuilder.forPort(properties.port())
                .addService(ServerInterceptors.intercept(
                        classroomAuthorizationGrpcService,
                        exceptionHandlingInterceptor,
                        internalApiKeyInterceptor
                ))
                .build()
                .start();

        log.info("gRPC authorization server started on port {}", properties.port());
    }

    @PreDestroy
    void stop() {
        if (server != null) {
            log.info("Stopping gRPC authorization server");
            server.shutdown();
        }
    }
}

