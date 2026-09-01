package com.timcritt.tfg.infrastructure.kafka;

import java.time.Instant;
import java.util.List;

public record MaterialDetailsRequestMessage(
        String requestId,
        List<Long> materialIds,
        Instant requestedAt
) {
}

