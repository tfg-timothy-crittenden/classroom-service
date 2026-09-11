package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.ClassroomManagementAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserTeacherRoleRevokedEventListener {

    private final ObjectMapper objectMapper;
    private final ClassroomManagementAdapter classroomService;

    public UserTeacherRoleRevokedEventListener(ObjectMapper objectMapper, ClassroomManagementAdapter classroomService) {
        this.objectMapper = objectMapper;
        this.classroomService = classroomService;
    }

    @KafkaListener(
            topics = "${classroom.kafka.user-teacher-role-revoked-topic:user.teacher-role-revoked.v1}",
            groupId = "${classroom.kafka.user-teacher-role-revoked-group-id:classroom-service-user-teacher-role-revoked}",
            containerFactory = "classroomIntegrationKafkaListenerContainerFactory"
    )
    public void onUserTeacherRoleRevoked(String payload) {
        if (payload == null) {
            throw new InvalidIntegrationEventException("User teacher role revoked event payload is required");
        }
        try {
            UserTeacherRoleRevokedEvent event = objectMapper.readValue(payload, UserTeacherRoleRevokedEvent.class);
            if (event == null || event.userId() == null) {
                throw new InvalidIntegrationEventException("User teacher role revoked event requires userId");
            }

            int updated = classroomService.revokeTeacherRoleFromUser(event.userId());
            log.info(
                    "Processed user teacher role revoked event for userId={}, updatedClassrooms={}",
                    event.userId(),
                    updated
            );
        } catch (JsonProcessingException ex) {
            throw new InvalidIntegrationEventException("Failed to parse user teacher role revoked event", ex);
        }
    }
}

