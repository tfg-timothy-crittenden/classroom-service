package com.timcritt.tfg.application.port.outbound;

public record IntegrationOutboxMessage(
        String aggregateType,
        Long aggregateId,
        String eventType,
        String eventKey,
        Object payload
) {
}

