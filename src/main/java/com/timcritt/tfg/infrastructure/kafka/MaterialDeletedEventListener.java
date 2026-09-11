package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.application.port.inbound.MaterialDeletionProjectionUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MaterialDeletedEventListener {

    private final ObjectMapper objectMapper;
    private final MaterialDeletionProjectionUseCase deletionUseCase;

    public MaterialDeletedEventListener(
            ObjectMapper objectMapper,
            MaterialDeletionProjectionUseCase deletionUseCase
    ) {
        this.objectMapper = objectMapper;
        this.deletionUseCase = deletionUseCase;
    }

    @KafkaListener(
            topics = "${classroom.kafka.material-deleted-topic:material.deleted.v1}",
            groupId = "${classroom.kafka.material-deleted-group-id:classroom-service-material-deleted}",
            containerFactory = "classroomIntegrationKafkaListenerContainerFactory"
    )
    public void onMaterialDeleted(String payload) {
        if (payload == null) {
            throw new InvalidIntegrationEventException("Material deleted event payload is required");
        }
        MaterialDeletedEvent event;
        try {
            event = objectMapper.readValue(payload, MaterialDeletedEvent.class);
        } catch (JsonProcessingException ex) {
            throw new InvalidIntegrationEventException("Failed to parse material deleted event", ex);
        }
        if (event == null || event.materialId() == null || event.materialId() <= 0) {
            throw new InvalidIntegrationEventException("Material deleted event requires a positive materialId");
        }

        deletionUseCase.handleMaterialDeleted(event.materialId());
        log.info(
                "Processed material deleted event for materialId={}, rootNodeId={}, deletedAt={}",
                event.materialId(),
                event.rootNodeId(),
                event.deletedAt()
        );
    }
}
