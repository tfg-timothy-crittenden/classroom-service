package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.application.port.outbound.MaterialDetailsRequestPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class MaterialDetailsRequestPublisherAdapter implements MaterialDetailsRequestPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String materialDetailsRequestedTopic;

    public MaterialDetailsRequestPublisherAdapter(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${classroom.kafka.material-details-requested-topic:material.details.requested.v1}") String materialDetailsRequestedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.materialDetailsRequestedTopic = materialDetailsRequestedTopic;
    }

    @Override
    public void requestMaterialDetails(List<Long> materialIds) {
        if (materialIds == null || materialIds.isEmpty()) {
            return;
        }

        MaterialDetailsRequestMessage message = new MaterialDetailsRequestMessage(
                UUID.randomUUID().toString(),
                materialIds,
                Instant.now()
        );

        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(materialDetailsRequestedTopic, payload);
            log.info(
                    "Published material details request requestId={}, materialIdsCount={}, materialIds={}, topic={}",
                    message.requestId(),
                    message.materialIds().size(),
                    message.materialIds(),
                    materialDetailsRequestedTopic
            );
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize material details request payload for materialIds={}", materialIds, ex);
        } catch (RuntimeException ex) {
            // Material assignments are already persisted; keep request publish best-effort.
            log.error("Failed to publish material details request for materialIds={} to topic={}", materialIds, materialDetailsRequestedTopic, ex);
        }
    }
}

