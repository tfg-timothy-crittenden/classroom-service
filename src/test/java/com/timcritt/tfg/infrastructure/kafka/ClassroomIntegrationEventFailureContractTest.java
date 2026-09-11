package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.application.port.inbound.MaterialDeletionProjectionUseCase;
import com.timcritt.tfg.infrastructure.service.ClassroomManagementAdapter;
import com.timcritt.tfg.infrastructure.service.MaterialDetailsUpdateServiceAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Broker-free listener boundary contract for invalid events and retryable service failures. */
class ClassroomIntegrationEventFailureContractTest {

    static final String FACTORY = "classroomIntegrationKafkaListenerContainerFactory";
    static final String INVALID_EVENT = "com.timcritt.tfg.infrastructure.kafka.InvalidIntegrationEventException";

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final MaterialDeletionProjectionUseCase deletion = mock(MaterialDeletionProjectionUseCase.class);
    private final MaterialDetailsUpdateServiceAdapter details = mock(MaterialDetailsUpdateServiceAdapter.class);
    private final ClassroomManagementAdapter classrooms = mock(ClassroomManagementAdapter.class);

    @ParameterizedTest(name = "{0} selects the dedicated factory")
    @EnumSource(Listener.class)
    void selectsDedicatedFactory(Listener listener) throws Exception {
        KafkaListener annotation = listener.type.getMethod(listener.method, String.class)
                .getAnnotation(KafkaListener.class);
        assertNotNull(annotation);
        assertEquals(FACTORY, annotation.containerFactory());
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("invalidEvents")
    void invalidEventMustEscapeInsteadOfBeingConsumed(Listener listener, String reason, String payload) {
        // Invoke first so silent consumption cannot satisfy the exception contract.
        RuntimeException failure = assertThrows(RuntimeException.class, () -> invocation(listener).accept(payload));
        assertAll(
                () -> assertEquals(INVALID_EVENT, failure.getClass().getName()),
                () -> verifyNoInteractions(deletion, details, classrooms),
                () -> {
                    if (reason.equals("malformed JSON")) {
                        assertInstanceOf(JsonProcessingException.class, failure.getCause());
                    }
                }
        );
    }

    static Stream<Arguments> invalidEvents() {
        Stream<Arguments> common = Stream.of(Listener.values()).flatMap(listener -> Stream.of(
                Arguments.of(listener, "malformed JSON", "not-json"),
                Arguments.of(listener, "null payload", null),
                Arguments.of(listener, "empty payload", ""),
                Arguments.of(listener, "blank payload", "   "),
                Arguments.of(listener, "scalar instead of event object", "42"),
                Arguments.of(listener, "array instead of event object", "[]"),
                Arguments.of(listener, "JSON null instead of event object", "null"),
                Arguments.of(listener, "missing required ID", "{\"version\":0,\"materialTitle\":\"Title\"}"),
                Arguments.of(listener, "null required ID", "{\"" + listener.id + "\":null,\"version\":0,\"materialTitle\":\"Title\"}")
        ));
        Stream<Arguments> versions = Stream.of(Listener.DETAILS_UPSERTED, Listener.TITLE_UPDATED)
                .flatMap(listener -> Stream.of(
                        Arguments.of(listener, "missing version", "{\"materialId\":26,\"materialTitle\":\"Title\"}"),
                        Arguments.of(listener, "null version", "{\"materialId\":26,\"version\":null,\"materialTitle\":\"Title\"}"),
                        Arguments.of(listener, "negative version", "{\"materialId\":26,\"version\":-1,\"materialTitle\":\"Title\"}")
                ));
        Stream<Arguments> envelopes = Stream.of(
                "{\"event\":[]}",
                "{\"payload\":{\"event\":null}}",
                "{\"payload\":{\"materialId\":26}}",
                "{\"event\":{\"materialId\":26,\"version\":-1}}"
        ).map(payload -> Arguments.of(Listener.DETAILS_UPSERTED, "invalid existing envelope: " + payload, payload));
        return Stream.concat(Stream.concat(common, versions), envelopes);
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("serviceFailures")
    void serviceFailuresEscapeUnchangedForRetry(Listener listener, RuntimeException failure) {
        switch (listener) {
            case DELETED -> doThrow(failure).when(deletion).handleMaterialDeleted(26L);
            case DETAILS_UPSERTED, TITLE_UPDATED -> doThrow(failure).when(details)
                    .updateDetails(26L, 0L, "Title", null, null, null);
            case ROLE_REVOKED -> doThrow(failure).when(classrooms).revokeTeacherRoleFromUser(26L);
        }

        String payload = listener == Listener.ROLE_REVOKED ? "{\"userId\":26}"
                : "{\"materialId\":26,\"version\":0,\"materialTitle\":\"Title\"}";
        assertSame(failure, assertThrows(RuntimeException.class, () -> invocation(listener).accept(payload)));
        if (listener == Listener.DELETED) {
            verify(deletion, times(1)).handleMaterialDeleted(26L);
            verifyNoMoreInteractions(deletion);
            verifyNoInteractions(details, classrooms);
        }
    }

    static Stream<Arguments> serviceFailures() {
        return Stream.of(Listener.values()).flatMap(listener -> Stream.of(
                new IllegalStateException("transient application failure"),
                new DataAccessResourceFailureException("database unavailable")
        ).map(failure -> Arguments.of(listener, failure)));
    }

    private Consumer<String> invocation(Listener listener) {
        return switch (listener) {
            case DELETED -> new MaterialDeletedEventListener(mapper, deletion)::onMaterialDeleted;
            case DETAILS_UPSERTED -> new MaterialDetailsUpsertedEventListener(mapper, details)::onMaterialDetailsUpserted;
            case TITLE_UPDATED -> new MaterialTitleUpdatedEventListener(mapper, details)::onMaterialTitleUpdated;
            case ROLE_REVOKED -> new UserTeacherRoleRevokedEventListener(mapper, classrooms)::onUserTeacherRoleRevoked;
        };
    }

    enum Listener {
        DELETED(MaterialDeletedEventListener.class, "onMaterialDeleted", "materialId"),
        DETAILS_UPSERTED(MaterialDetailsUpsertedEventListener.class, "onMaterialDetailsUpserted", "materialId"),
        TITLE_UPDATED(MaterialTitleUpdatedEventListener.class, "onMaterialTitleUpdated", "materialId"),
        ROLE_REVOKED(UserTeacherRoleRevokedEventListener.class, "onUserTeacherRoleRevoked", "userId");

        final Class<?> type;
        final String method;
        final String id;

        Listener(Class<?> type, String method, String id) {
            this.type = type;
            this.method = method;
            this.id = id;
        }
    }
}
