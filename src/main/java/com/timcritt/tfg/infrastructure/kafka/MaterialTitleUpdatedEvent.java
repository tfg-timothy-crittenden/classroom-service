package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MaterialTitleUpdatedEvent(
        Long materialId,
        String materialTitle,
        String part1Title,
        String part2Title,
        Instant updatedAt
) {
}
