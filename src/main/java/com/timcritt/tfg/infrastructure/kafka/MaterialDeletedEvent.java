package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MaterialDeletedEvent(
        Long materialId,
        Long rootNodeId,
        Instant deletedAt
) {
}
