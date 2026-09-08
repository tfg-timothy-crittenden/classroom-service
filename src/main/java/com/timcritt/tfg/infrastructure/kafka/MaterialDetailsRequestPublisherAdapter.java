package com.timcritt.tfg.infrastructure.kafka;

import com.timcritt.tfg.application.event.ClassroomIntegrationEventTypes;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxMessage;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxPort;
import com.timcritt.tfg.application.port.outbound.MaterialDetailsRequestPublisherPort;
import com.timcritt.tfg.domain.aggregate.classroom.event.MaterialDetailsRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class MaterialDetailsRequestPublisherAdapter implements MaterialDetailsRequestPublisherPort {

    private final IntegrationOutboxPort integrationOutbox;

    public MaterialDetailsRequestPublisherAdapter(IntegrationOutboxPort integrationOutbox) {
        this.integrationOutbox = integrationOutbox;
    }

    @Override
    public void requestMaterialDetails(List<Long> materialIds) {
        if (materialIds == null || materialIds.isEmpty()) {
            return;
        }

        List<Long> normalizedIds = materialIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        if (normalizedIds.isEmpty()) {
            return;
        }

        MaterialDetailsRequestedEvent event = new MaterialDetailsRequestedEvent(
                UUID.randomUUID().toString(),
                normalizedIds,
                Instant.now()
        );

        integrationOutbox.append(new IntegrationOutboxMessage(
                "classroom",
                normalizedIds.getFirst(),
                ClassroomIntegrationEventTypes.MATERIAL_DETAILS_REQUESTED_V1,
                event.requestId(),
                event
        ));

        log.info(
                "Appended material details request to outbox requestId={}, materialIdsCount={}, materialIds={}",
                event.requestId(),
                event.materialIds().size(),
                event.materialIds()
        );
    }
}
