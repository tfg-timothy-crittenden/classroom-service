package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.inbound.MaterialDeletionProjectionUseCase;
import com.timcritt.tfg.application.port.outbound.repository.MaterialDetailsRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.service.useCase.MaterialDeletionProjectionUseCaseImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialDeletionServiceAdapter implements MaterialDeletionProjectionUseCase {

    private final MaterialDeletionProjectionUseCase delegate;

    public MaterialDeletionServiceAdapter(
            MaterialReferenceRepositoryPort materialReferences,
            MaterialDetailsRepositoryPort materialDetails
    ) {
        this.delegate = new MaterialDeletionProjectionUseCaseImpl(materialReferences, materialDetails);
    }

    @Override
    @Transactional
    public void handleMaterialDeleted(Long materialId) {
        delegate.handleMaterialDeleted(materialId);
    }
}
