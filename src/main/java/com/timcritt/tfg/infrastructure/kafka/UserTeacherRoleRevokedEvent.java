package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserTeacherRoleRevokedEvent(
        Long userId
) {
}

