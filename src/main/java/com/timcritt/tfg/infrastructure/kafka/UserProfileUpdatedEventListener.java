package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.MemberProfileProjectionServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserProfileUpdatedEventListener {

    private final ObjectMapper objectMapper;
    private final MemberProfileProjectionServiceAdapter profileService;

    public UserProfileUpdatedEventListener(
            ObjectMapper objectMapper,
            MemberProfileProjectionServiceAdapter profileService
    ) {
        this.objectMapper = objectMapper;
        this.profileService = profileService;
    }

    @KafkaListener(
            topics = "${classroom.kafka.user-profile-updated-topic:user.profile-updated.v1}",
            groupId = "${classroom.kafka.user-profile-updated-group-id:classroom-service-user-profile-updated}",
            containerFactory = "classroomIntegrationKafkaListenerContainerFactory"
    )
    public void onUserProfileUpdated(String payload) {
        if (payload == null) {
            throw new InvalidIntegrationEventException(
                    "User profile updated event payload is required"
            );
        }

        try {
            UserProfileUpdatedEvent event =
                    objectMapper.readValue(
                            payload,
                            UserProfileUpdatedEvent.class
                    );

            if (event == null
                    || event.userId() == null
                    || event.version() == null) {
                throw new InvalidIntegrationEventException(
                        "User profile updated event requires userId and version"
                );
            }

            profileService.updateProfile(
                    event.userId(),
                    event.version(),
                    event.firstName(),
                    event.lastName()
            );

            log.info(
                    "Processed user profile updated event userId={}, version={}",
                    event.userId(),
                    event.version()
            );

        } catch (JsonProcessingException ex) {
            throw new InvalidIntegrationEventException(
                    "Failed to parse user profile updated event",
                    ex
            );
        }
    }
}