package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.ClassroomServiceAdapter;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserTeacherRoleRevokedEventListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClassroomServiceAdapter classroomService = mock(ClassroomServiceAdapter.class);
    private final UserTeacherRoleRevokedEventListener listener = new UserTeacherRoleRevokedEventListener(objectMapper, classroomService);

    @Test
    void delegatesTeacherRoleRevocationWhenUserIdIsPresent() {
        when(classroomService.revokeTeacherRoleFromUser(2L)).thenReturn(3);

        listener.onUserTeacherRoleRevoked("{\"userId\":2,\"ignored\":true}");

        verify(classroomService).revokeTeacherRoleFromUser(2L);
    }

    @Test
    void ignoresPayloadWithoutUserId() {
        listener.onUserTeacherRoleRevoked("{\"ignored\":true}");

        verifyNoInteractions(classroomService);
    }

    @Test
    void ignoresMalformedPayload() {
        listener.onUserTeacherRoleRevoked("not-json");

        verifyNoInteractions(classroomService);
    }
}

