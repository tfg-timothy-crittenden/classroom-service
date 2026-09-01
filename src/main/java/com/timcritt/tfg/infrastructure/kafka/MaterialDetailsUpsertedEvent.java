package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MaterialDetailsUpsertedEvent(
        Long materialId,
        Long version,
        @JsonAlias({"materialTitle", "title", "name"})
        String materialTitle,
        String part1Title,
        String part2Title,
        String description,
        Instant updatedAt
) {
}

