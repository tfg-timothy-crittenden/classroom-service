package com.timcritt.tfg.infrastructure.kafka;

import com.timcritt.tfg.application.event.ClassroomIntegrationEventTypes;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxMessage;
import com.timcritt.tfg.application.port.outbound.IntegrationOutboxPort;
import com.timcritt.tfg.application.port.outbound.UserProfileDetailsRequestPublisherPort;
import com.timcritt.tfg.domain.aggregate.classroom.event.UserProfileDetailsRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class UserProfileDetailsRequestPublisherAdapter implements UserProfileDetailsRequestPublisherPort {

    private final IntegrationOutboxPort integrationOutbox;

    public UserProfileDetailsRequestPublisherAdapter(
            IntegrationOutboxPort integrationOutbox
    ) {
        this.integrationOutbox = integrationOutbox;
    }

    @Override
    public void requestUserProfileDetails(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        List<Long> normalizedIds = userIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        if (normalizedIds.isEmpty()) {
            return;
        }

        UserProfileDetailsRequestedEvent event =
                new UserProfileDetailsRequestedEvent(
                        UUID.randomUUID().toString(),
                        normalizedIds,
                        Instant.now()
                );

        integrationOutbox.append(
                new IntegrationOutboxMessage(
                        "classroom",
                        normalizedIds.getFirst(),
                        ClassroomIntegrationEventTypes.USER_PROFILE_DETAILS_REQUESTED_V1,
                        event.requestId(),
                        event
                )
        );
    }
}