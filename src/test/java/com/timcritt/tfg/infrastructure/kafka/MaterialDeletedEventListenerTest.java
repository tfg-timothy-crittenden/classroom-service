package com.timcritt.tfg.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timcritt.tfg.infrastructure.service.MaterialDetailsUpdateServiceAdapter;
import com.timcritt.tfg.infrastructure.service.MaterialReferenceDeletionServiceAdapter;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MaterialDeletedEventListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MaterialReferenceDeletionServiceAdapter materialReferenceDeletionService = mock(MaterialReferenceDeletionServiceAdapter.class);
    private final MaterialDetailsUpdateServiceAdapter materialDetailsService = mock(MaterialDetailsUpdateServiceAdapter.class);
    private final MaterialDeletedEventListener listener = new MaterialDeletedEventListener(
            objectMapper,
            materialReferenceDeletionService,
            materialDetailsService
    );

    @Test
    void delegatesDeleteToReferencesAndDetailsWhenMaterialIdIsPresent() {
        when(materialReferenceDeletionService.deleteByMaterialId(2L)).thenReturn(3);

        listener.onMaterialDeleted("{\"materialId\":2,\"rootNodeId\":9}");

        verify(materialReferenceDeletionService).deleteByMaterialId(2L);
        verify(materialDetailsService).deleteByMaterialId(2L);
    }

    @Test
    void ignoresPayloadWithoutMaterialId() {
        listener.onMaterialDeleted("{\"rootNodeId\":9}");

        verifyNoInteractions(materialReferenceDeletionService);
        verifyNoInteractions(materialDetailsService);
    }

    @Test
    void ignoresMalformedPayload() {
        listener.onMaterialDeleted("not-json");

        verifyNoInteractions(materialReferenceDeletionService);
        verifyNoInteractions(materialDetailsService);
    }
}

