package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.MaterialReferenceDeletionServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MaterialDeletedEventListener {

    private final ObjectMapper objectMapper;
    private final MaterialReferenceDeletionServiceAdapter deletionService;

    public MaterialDeletedEventListener(ObjectMapper objectMapper, MaterialReferenceDeletionServiceAdapter deletionService) {
        this.objectMapper = objectMapper;
        this.deletionService = deletionService;
    }

    @KafkaListener(
            topics = "${classroom.kafka.material-deleted-topic:material.deleted.v1}",
            groupId = "${classroom.kafka.material-deleted-group-id:classroom-service-material-deleted}"
    )
    public void onMaterialDeleted(String payload) {
        try {
            MaterialDeletedEvent event = objectMapper.readValue(payload, MaterialDeletedEvent.class);
            if (event.materialId() == null) {
                log.warn("Ignoring material deleted event without materialId: {}", payload);
                return;
            }

            int removed = deletionService.deleteByMaterialId(event.materialId());
            log.info(
                    "Processed material deleted event for materialId={}, rootNodeId={}, deletedAt={}, removedReferences={}",
                    event.materialId(),
                    event.rootNodeId(),
                    event.deletedAt(),
                    removed
            );
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse material deleted event payload: {}", payload, ex);
        }
    }
}
