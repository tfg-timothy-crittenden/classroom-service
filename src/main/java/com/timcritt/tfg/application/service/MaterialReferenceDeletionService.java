package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;

/**
 * Application service (no Spring) that removes classroom references when a material is deleted upstream.
 */
public class MaterialReferenceDeletionService {

    private final MaterialReferenceCommandPort commandPort;

    public MaterialReferenceDeletionService(MaterialReferenceCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    public int deleteByMaterialId(Long materialId) {
        if (materialId == null) {
            throw new IllegalArgumentException("materialId is required");
        }
        return commandPort.deleteByMaterialId(materialId);
    }
}
