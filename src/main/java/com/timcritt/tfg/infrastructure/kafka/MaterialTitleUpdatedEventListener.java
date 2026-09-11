package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.MaterialDetailsUpdateServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MaterialTitleUpdatedEventListener {

    private final ObjectMapper objectMapper;
    private final MaterialDetailsUpdateServiceAdapter titleUpdateService;

    public MaterialTitleUpdatedEventListener(ObjectMapper objectMapper, MaterialDetailsUpdateServiceAdapter titleUpdateService) {
        this.objectMapper = objectMapper;
        this.titleUpdateService = titleUpdateService;
    }

    @KafkaListener(
            topics = "${classroom.kafka.material-titles-updated-topic:material.titles.updated.v1}",
            groupId = "${classroom.kafka.material-titles-updated-group-id:classroom-service-material-titles-updated}",
            containerFactory = "classroomIntegrationKafkaListenerContainerFactory"
    )
    public void onMaterialTitleUpdated(String payload) {
        if (payload == null) {
            throw new InvalidIntegrationEventException("Material title updated event payload is required");
        }
        try {
            MaterialTitleUpdatedEvent event = objectMapper.readValue(payload, MaterialTitleUpdatedEvent.class);
            if (event == null || event.materialId() == null) {
                throw new InvalidIntegrationEventException("Material title updated event requires materialId");
            }
            if (event.version() == null || event.version() < 0) {
                throw new InvalidIntegrationEventException("Material title updated event requires a non-negative version");
            }
            if (isBlank(event.materialTitle()) && isBlank(event.part1Title()) && isBlank(event.part2Title()) && isBlank(event.description())) {
                log.warn("Ignoring material title updated event without any title fields for materialId={}", event.materialId());
                return;
            }

            titleUpdateService.updateDetails(
                    event.materialId(),
                    event.version(),
                    event.materialTitle(),
                    event.part1Title(),
                    event.part2Title(),
                    event.description()
            );
            log.info(
                    "Processed material title updated event for materialId={}, version={}, updatedAt={}, part1Title={}, part2Title={}, description={}",
                    event.materialId(),
                    event.version(),
                    event.updatedAt(),
                    event.part1Title(),
                    event.part2Title(),
                    event.description()

            );
        } catch (JsonProcessingException ex) {
            throw new InvalidIntegrationEventException("Failed to parse material title updated event", ex);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
