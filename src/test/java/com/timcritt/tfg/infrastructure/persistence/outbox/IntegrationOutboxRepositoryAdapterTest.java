package com.timcritt.tfg.infrastructure.persistence.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxMessage;
import com.timcritt.tfg.domain.aggregate.classroom.event.MaterialDetailsRequestedEvent;
import com.timcritt.tfg.infrastructure.persistence.jpa.IntegrationOutboxJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.IntegrationOutboxJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IntegrationOutboxRepositoryAdapterTest {

    private final IntegrationOutboxJpaRepository outboxRepository = mock(IntegrationOutboxJpaRepository.class);
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    private final IntegrationOutboxRepositoryAdapter adapter = new IntegrationOutboxRepositoryAdapter(outboxRepository, objectMapper);

    @Test
    void appendSerializesPayloadAndPersistsOutboxRow() {
        MaterialDetailsRequestedEvent payload = new MaterialDetailsRequestedEvent(
                "req-1",
                List.of(10L, 20L),
                Instant.parse("2026-09-08T18:00:00Z")
        );

        adapter.append(new IntegrationOutboxMessage("classroom", 7L, "material.details.requested.v1", "req-1", payload));

        ArgumentCaptor<IntegrationOutboxJpaEntity> captor = ArgumentCaptor.forClass(IntegrationOutboxJpaEntity.class);
        verify(outboxRepository).save(captor.capture());
        IntegrationOutboxJpaEntity saved = captor.getValue();

        assertEquals("classroom", saved.getAggregateType());
        assertEquals(7L, saved.getAggregateId());
        assertEquals("material.details.requested.v1", saved.getEventType());
        assertEquals("req-1", saved.getEventKey());
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getPayload().contains("\"requestId\":\"req-1\""));
        assertTrue(saved.getPayload().contains("\"materialIds\":[10,20]"));
    }
}

