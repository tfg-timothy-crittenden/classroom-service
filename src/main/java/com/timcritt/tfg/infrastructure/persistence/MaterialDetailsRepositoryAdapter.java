package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.repository.MaterialDetailsRepositoryPort;
import com.timcritt.tfg.domain.projection.MaterialDetails;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialDetailsJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.MaterialDetailsJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MaterialDetailsRepositoryAdapter implements MaterialDetailsRepositoryPort {

    private final MaterialDetailsJpaRepository materialDetailsJpaRepository;

    public MaterialDetailsRepositoryAdapter(MaterialDetailsJpaRepository materialDetailsJpaRepository) {
        this.materialDetailsJpaRepository = materialDetailsJpaRepository;
    }

    @Override
    public MaterialDetails findByMaterialId(Long id) {
        if (id == null) {
            return null;
        }

        return materialDetailsJpaRepository.findById(id)
                .map(MaterialDetailsEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional
    public void save(MaterialDetails materialDetails) {
        MaterialDetailsJpaEntity entity = MaterialDetailsEntityMapper.toJpa(materialDetails);
        materialDetailsJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteByMaterialId(Long materialId) {
        materialDetailsJpaRepository.deleteById(materialId);
    }
}

