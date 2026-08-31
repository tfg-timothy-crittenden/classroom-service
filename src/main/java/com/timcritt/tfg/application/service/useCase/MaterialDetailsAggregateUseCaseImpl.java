package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.port.inbound.MaterialDetailsAggregateUseCase;
import com.timcritt.tfg.application.port.outbound.repository.MaterialDetailsRepositoryPort;
import com.timcritt.tfg.domain.model.MaterialDetails;


public class MaterialDetailsAggregateUseCaseImpl implements MaterialDetailsAggregateUseCase {

    private final MaterialDetailsRepositoryPort materialDetailsRepository;

    public MaterialDetailsAggregateUseCaseImpl(MaterialDetailsRepositoryPort repository) {
        this.materialDetailsRepository = repository;
    }


    @Override
    public void deleteByMaterialId(Long materialId) {
        if (materialId == null) {
            throw new IllegalArgumentException("materialId is required");
        }
        materialDetailsRepository.deleteByMaterialId(materialId);
    }

    @Override
    public void updateDetails(Long materialId, Long version, String title, String part1Title, String part2Title, String description) {
        if (materialId == null) {
            throw new IllegalArgumentException("materialId is required");
        }
        if (version == null) {
            throw new IllegalArgumentException("version is required");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version cannot be negative");
        }

        MaterialDetails materialDetails = materialDetailsRepository.findByMaterialId(materialId);
        if (materialDetails == null) {
            MaterialDetails newMaterialDetails = MaterialDetails.builder()
                    .materialId(materialId)
                    .version(version)
                    .name(title)
                    .description(description)
                    .part1Title(part1Title)
                    .part2Title(part2Title)
                    .build();
            materialDetailsRepository.save(newMaterialDetails);
            return;
        }

        long currentVersion = materialDetails.getVersion() == null ? 0L : materialDetails.getVersion();
        if (version <= currentVersion) {
            return;
        }
        materialDetails.updateDetails(title, description, part1Title, part2Title);
        materialDetails.updateVersion(version);
        materialDetailsRepository.save(materialDetails);
    }

}

