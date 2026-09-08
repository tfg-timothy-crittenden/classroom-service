package com.timcritt.tfg.infrastructure.kafka;

import com.timcritt.tfg.application.event.ClassroomIntegrationEventTypes;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxMessage;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxPort;
import com.timcritt.tfg.domain.aggregate.classroom.event.MaterialDetailsRequestedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class MaterialDetailsRequestPublisherAdapterTest {

    private final IntegrationOutboxPort outboxPort = mock(IntegrationOutboxPort.class);
    private final MaterialDetailsRequestPublisherAdapter adapter = new MaterialDetailsRequestPublisherAdapter(outboxPort);

    @Test
    void appendsOutboxEventForValidIds() {
        adapter.requestMaterialDetails(Arrays.asList(5L, 5L, null, -1L, 7L));

        ArgumentCaptor<IntegrationOutboxMessage> captor = ArgumentCaptor.forClass(IntegrationOutboxMessage.class);
        verify(outboxPort).append(captor.capture());
        IntegrationOutboxMessage event = captor.getValue();

        assertEquals("classroom", event.aggregateType());
        assertEquals(5L, event.aggregateId());
        assertEquals(ClassroomIntegrationEventTypes.MATERIAL_DETAILS_REQUESTED_V1, event.eventType());
        MaterialDetailsRequestedEvent payload = org.junit.jupiter.api.Assertions.assertInstanceOf(
                MaterialDetailsRequestedEvent.class,
                event.payload()
        );
        assertEquals(List.of(5L, 7L), payload.materialIds());
    }

    @Test
    void doesNothingWhenNoValidIds() {
        adapter.requestMaterialDetails(Arrays.asList(null, -2L));

        verifyNoInteractions(outboxPort);
    }
}

