package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.repository.MaterialReferenceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialReferenceDeletionServiceAdapter {

    private final MaterialReferenceRepositoryPort repository;

    public MaterialReferenceDeletionServiceAdapter(MaterialReferenceRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public int deleteByMaterialId(Long materialId) {
        return repository.deleteByMaterialId(materialId);
    }
}
