package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.inbound.MaterialDetailsAggregateUseCase;
import com.timcritt.tfg.infrastructure.persistence.MaterialDetailsRepositoryAdapter;
import com.timcritt.tfg.application.service.useCase.MaterialDetailsAggregateUseCaseImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialDetailsUpdateServiceAdapter {

    private final MaterialDetailsAggregateUseCase delegate;

    public MaterialDetailsUpdateServiceAdapter(MaterialDetailsRepositoryAdapter repository) {
        this.delegate = new MaterialDetailsAggregateUseCaseImpl(repository);
    }

    @Transactional
    public void updateDetails(Long materialId, Long version, String title, String part1Title, String part2Title, String description) {
        delegate.updateDetails(materialId, version, title, part1Title, part2Title, description);
    }
}
