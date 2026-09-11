package com.timcritt.tfg.application;

import com.timcritt.tfg.application.port.outbound.repository.MaterialDetailsRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.service.useCase.MaterialDeletionProjectionUseCaseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaterialDeletionProjectionUseCaseImplTest {

    private final MaterialReferenceRepositoryPort references = mock(MaterialReferenceRepositoryPort.class);
    private final MaterialDetailsRepositoryPort details = mock(MaterialDetailsRepositoryPort.class);
    private final MaterialDeletionProjectionUseCaseImpl useCase = new MaterialDeletionProjectionUseCaseImpl(references, details);

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    void deletesReferencesThenDetailsEvenWhenNoReferencesRemain(int deletedReferences) {
        when(references.deleteByMaterialId(26L)).thenReturn(deletedReferences);

        useCase.handleMaterialDeleted(26L);

        var order = inOrder(references, details);
        order.verify(references).deleteByMaterialId(26L);
        order.verify(details).deleteByMaterialId(26L);
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void rejectsInvalidMaterialIdsBeforeAnyRepositoryCall(Long materialId) {
        assertThrows(IllegalArgumentException.class, () -> useCase.handleMaterialDeleted(materialId));
        verifyNoInteractions(references, details);
    }

    @Test
    void duplicateDeletionIsSuccessfulWhenBothProjectionsAreAlreadyAbsent() {
        when(references.deleteByMaterialId(26L)).thenReturn(3, 0);

        assertDoesNotThrow(() -> useCase.handleMaterialDeleted(26L));
        assertDoesNotThrow(() -> useCase.handleMaterialDeleted(26L));

        var order = inOrder(references, details);
        order.verify(references).deleteByMaterialId(26L);
        order.verify(details).deleteByMaterialId(26L);
        order.verify(references).deleteByMaterialId(26L);
        order.verify(details).deleteByMaterialId(26L);
        order.verifyNoMoreInteractions();
    }

    @Test
    void referenceDeletionFailureEscapesAndPreventsDetailsDeletion() {
        RuntimeException failure = new IllegalStateException("reference deletion failed");
        when(references.deleteByMaterialId(26L)).thenThrow(failure);

        assertSame(failure, assertThrows(RuntimeException.class, () -> useCase.handleMaterialDeleted(26L)));
        verify(references).deleteByMaterialId(26L);
        verifyNoInteractions(details);
    }

    @Test
    void detailsDeletionFailureEscapesAfterReferenceDeletion() {
        RuntimeException failure = new IllegalStateException("details deletion failed");
        doThrow(failure).when(details).deleteByMaterialId(26L);

        assertSame(failure, assertThrows(RuntimeException.class, () -> useCase.handleMaterialDeleted(26L)));
        var order = inOrder(references, details);
        order.verify(references).deleteByMaterialId(26L);
        order.verify(details).deleteByMaterialId(26L);
        order.verifyNoMoreInteractions();
    }
}
