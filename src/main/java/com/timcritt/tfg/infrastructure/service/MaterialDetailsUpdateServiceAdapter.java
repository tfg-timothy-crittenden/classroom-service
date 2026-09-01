package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.inbound.MaterialDetailsProjectionUseCase;
import com.timcritt.tfg.infrastructure.persistence.MaterialDetailsRepositoryAdapter;
import com.timcritt.tfg.application.service.useCase.MaterialDetailsProjectionUseCaseImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialDetailsUpdateServiceAdapter {

    private final MaterialDetailsProjectionUseCase delegate;

    public MaterialDetailsUpdateServiceAdapter(MaterialDetailsRepositoryAdapter repository) {
        this.delegate = new MaterialDetailsProjectionUseCaseImpl(repository);
    }

    @Transactional
    public void updateDetails(Long materialId, Long version, String title, String part1Title, String part2Title, String description) {
        delegate.updateDetails(materialId, version, title, part1Title, part2Title, description);
    }

    @Transactional
    public void deleteByMaterialId(Long materialId) {
        delegate.deleteByMaterialId(materialId);
    }
}
