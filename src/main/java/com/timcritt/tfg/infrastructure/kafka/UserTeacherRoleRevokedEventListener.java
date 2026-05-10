package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.ClassroomServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserTeacherRoleRevokedEventListener {

    private final ObjectMapper objectMapper;
    private final ClassroomServiceAdapter classroomService;

    public UserTeacherRoleRevokedEventListener(ObjectMapper objectMapper, ClassroomServiceAdapter classroomService) {
        this.objectMapper = objectMapper;
        this.classroomService = classroomService;
    }

    @KafkaListener(
            topics = "${classroom.kafka.user-teacher-role-revoked-topic:user.teacher-role-revoked.v1}",
            groupId = "${classroom.kafka.user-teacher-role-revoked-group-id:classroom-service-user-teacher-role-revoked}"
    )
    public void onUserTeacherRoleRevoked(String payload) {
        try {
            UserTeacherRoleRevokedEvent event = objectMapper.readValue(payload, UserTeacherRoleRevokedEvent.class);
            if (event.userId() == null) {
                log.warn("Ignoring user teacher role revoked event without userId: {}", payload);
                return;
            }

            int updated = classroomService.revokeTeacherRoleFromUser(event.userId());
            log.info(
                    "Processed user teacher role revoked event for userId={}, updatedClassrooms={}",
                    event.userId(),
                    updated
            );
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse user teacher role revoked event payload: {}", payload, ex);
        }
    }
}

