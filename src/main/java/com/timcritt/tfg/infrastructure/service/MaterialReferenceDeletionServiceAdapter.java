package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.application.service.MaterialReferenceDeletionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialReferenceDeletionServiceAdapter {

    private final MaterialReferenceDeletionService delegate;

    public MaterialReferenceDeletionServiceAdapter(MaterialReferenceCommandPort commandPort) {
        this.delegate = new MaterialReferenceDeletionService(commandPort);
    }

    @Transactional
    public int deleteByMaterialId(Long materialId) {
        return delegate.deleteByMaterialId(materialId);
    }
}
