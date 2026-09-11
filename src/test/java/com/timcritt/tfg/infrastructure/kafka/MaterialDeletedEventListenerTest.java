package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.application.port.inbound.MaterialDeletionProjectionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MaterialDeletedEventListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MaterialDeletionProjectionUseCase deletionUseCase = mock(MaterialDeletionProjectionUseCase.class);
    private final MaterialDeletedEventListener listener = new MaterialDeletedEventListener(
            objectMapper,
            deletionUseCase
    );

    @Test
    void delegatesOnceToCombinedDeletionWhenMaterialIdIsPresent() {
        listener.onMaterialDeleted("{\"materialId\":2,\"rootNodeId\":9}");

        verify(deletionUseCase, times(1)).handleMaterialDeleted(2L);
        verifyNoMoreInteractions(deletionUseCase);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"not-json", "null", "{}", "{\"materialId\":0}", "{\"materialId\":-1}"})
    void rejectsInvalidEventsBeforeDelegation(String payload) {
        assertThrows(InvalidIntegrationEventException.class, () -> listener.onMaterialDeleted(payload));
        verifyNoInteractions(deletionUseCase);
    }

    @Test
    void combinedDeletionFailureEscapesUnchanged() {
        RuntimeException failure = new IllegalStateException("deletion failed");
        doThrow(failure).when(deletionUseCase).handleMaterialDeleted(2L);

        assertSame(failure, assertThrows(RuntimeException.class,
                () -> listener.onMaterialDeleted("{\"materialId\":2}")));
        verify(deletionUseCase, times(1)).handleMaterialDeleted(2L);
        verifyNoMoreInteractions(deletionUseCase);
    }
}

