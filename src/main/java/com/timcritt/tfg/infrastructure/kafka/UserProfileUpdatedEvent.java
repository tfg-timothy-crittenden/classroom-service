package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileUpdatedEvent(
        Long userId,
        Long version,
        String firstName,
        String lastName
) {
}