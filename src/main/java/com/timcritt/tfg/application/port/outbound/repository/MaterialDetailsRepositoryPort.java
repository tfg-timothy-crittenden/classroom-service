package com.timcritt.tfg.application.port.outbound.repository;

import com.timcritt.tfg.domain.model.MaterialDetails;


public interface MaterialDetailsRepositoryPort {

    MaterialDetails findByMaterialId(Long id);
    void save(MaterialDetails materialDetails);
    void deleteByMaterialId(Long materialId);
}
