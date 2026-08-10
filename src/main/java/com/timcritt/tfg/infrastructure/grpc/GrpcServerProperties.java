package com.timcritt.tfg.infrastructure.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "classroom.grpc")
public record GrpcServerProperties(
        boolean enabled,
        int port
) {
}

