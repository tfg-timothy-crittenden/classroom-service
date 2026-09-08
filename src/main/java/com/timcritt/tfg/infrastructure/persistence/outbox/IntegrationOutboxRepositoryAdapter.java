package com.timcritt.tfg.infrastructure.persistence.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxMessage;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxPort;
import com.timcritt.tfg.infrastructure.persistence.jpa.IntegrationOutboxJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.IntegrationOutboxJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public class IntegrationOutboxRepositoryAdapter implements IntegrationOutboxPort {

    private final IntegrationOutboxJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public IntegrationOutboxRepositoryAdapter(IntegrationOutboxJpaRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void append(IntegrationOutboxMessage message) {
        IntegrationOutboxJpaEntity entity = new IntegrationOutboxJpaEntity();
        entity.setAggregateType(message.aggregateType());
        entity.setAggregateId(message.aggregateId());
        entity.setEventType(message.eventType());
        entity.setEventKey(message.eventKey());
        entity.setPayload(toJson(message.payload()));
        entity.setCreatedAt(Instant.now());
        outboxRepository.save(entity);
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize outbox payload", ex);
        }
    }
}

