package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
            groupId = "${classroom.kafka.material-details-upserted-group-id:classroom-service-material-details-upserted}",
            containerFactory = "classroomIntegrationKafkaListenerContainerFactory"
    )
    public void onMaterialDetailsUpserted(String payload) {
        log.info("Received material details Kafka message payload={}", payload);
        try {
            MaterialDetailsUpsertedEvent event = parseEvent(payload);
            if (event == null || event.materialId() == null) {
                throw new InvalidIntegrationEventException("Material details upserted event requires materialId");
            }
            log.info(
                    "Parsed material details event materialId={}, version={}, updatedAt={}",
                    event.materialId(),
                    event.version(),
                    event.updatedAt()
            );
            if (event.version() == null || event.version() < 0) {
                throw new InvalidIntegrationEventException("Material details upserted event requires a non-negative version");
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
            throw new InvalidIntegrationEventException("Failed to parse material details upserted event", ex);
        }
    }

    private MaterialDetailsUpsertedEvent parseEvent(String payload) throws JsonProcessingException {
        if (payload == null) {
            throw new InvalidIntegrationEventException("Material details upserted event payload is required");
        }
        JsonNode root = objectMapper.readTree(payload);
        if (root == null || !root.isObject()) {
            throw new InvalidIntegrationEventException("Material details upserted event must be a JSON object");
        }

        // Some setups emit an envelope like {"event":{...},"requestId":"..."}.
        JsonNode rootEventNode = root.path("event");
        if (rootEventNode.isObject()) {
            return objectMapper.treeToValue(rootEventNode, MaterialDetailsUpsertedEvent.class);
        }

        // Debezium + outbox can wrap the event as {"schema":...,"payload":{"event":{...},...}}
        JsonNode wrappedEventNode = root.path("payload").path("event");
        if (wrappedEventNode.isObject()) {
            return objectMapper.treeToValue(wrappedEventNode, MaterialDetailsUpsertedEvent.class);
        }

        // Some setups emit {"payload":{...}} without an "event" nesting.
        JsonNode payloadNode = root.path("payload");
        if (payloadNode.isObject() && payloadNode.has("materialId")) {
            return objectMapper.treeToValue(payloadNode, MaterialDetailsUpsertedEvent.class);
        }

        // Fallback for direct event messages.
        return objectMapper.treeToValue(root, MaterialDetailsUpsertedEvent.class);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
