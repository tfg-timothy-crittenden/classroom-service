package com.timcritt.tfg.domain.aggregate.classroom.event;

import java.time.Instant;
import java.util.List;

public record UserProfileDetailsRequestedEvent(
        String requestId,
        List<Long> userIds,
        Instant requestedAt
) {
    public UserProfileDetailsRequestedEvent {
        userIds = userIds == null ? List.of() : List.copyOf(userIds);
    }
}