package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.MaterialDetailsUpdateServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MaterialDetailsUpsertedEventListener {

    private final ObjectMapper objectMapper;
    private final MaterialDetailsUpdateServiceAdapter materialDetailsUpdateService;

    public MaterialDetailsUpsertedEventListener(
            ObjectMapper objectMapper,
            MaterialDetailsUpdateServiceAdapter materialDetailsUpdateService
    ) {
        this.objectMapper = objectMapper;
        this.materialDetailsUpdateService = materialDetailsUpdateService;
    }

    @KafkaListener(
            topics = "${classroom.kafka.material-details-upserted-topic:material.details.upserted.v1}",
            groupId = "${classroom.kafka.material-details-upserted-group-id:classroom-service-material-details-upserted}"
    )
    public void onMaterialDetailsUpserted(String payload) {
        log.info("Received material details Kafka message payload={}", payload);
        try {
            MaterialDetailsUpsertedEvent event = objectMapper.readValue(payload, MaterialDetailsUpsertedEvent.class);
            log.info(
                    "Parsed material details request materialId={}, version={}, updatedAt={}",
                    event.materialId(),
                    event.version(),
                    event.updatedAt()
            );
            if (event.materialId() == null) {
                log.warn("Ignoring material details upserted event without materialId: {}", payload);
                return;
            }
            if (event.version() == null || event.version() < 0) {
                log.warn("Ignoring material details upserted event with invalid version for materialId={}: {}", event.materialId(), payload);
                return;
            }
            if (isBlank(event.materialTitle()) && isBlank(event.part1Title()) && isBlank(event.part2Title()) && isBlank(event.description())) {
                log.warn("Ignoring material details upserted event without any details fields for materialId={}", event.materialId());
                return;
            }

            materialDetailsUpdateService.updateDetails(
                    event.materialId(),
                    event.version(),
                    event.materialTitle(),
                    event.part1Title(),
                    event.part2Title(),
                    event.description()
            );
            log.info(
                    "Processed material details upserted event for materialId={}, version={}, updatedAt={}",
                    event.materialId(),
                    event.version(),
                    event.updatedAt()
            );
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse material details upserted event payload: {}", payload, ex);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

