package com.timcritt.tfg.domain.aggregate.classroom.event;

import java.time.Instant;
import java.util.List;

public record MaterialDetailsRequestedEvent(
        String requestId,
        List<Long> materialIds,
        Instant requestedAt
) {
    public MaterialDetailsRequestedEvent {
        materialIds = materialIds == null ? List.of() : List.copyOf(materialIds);
    }
}

