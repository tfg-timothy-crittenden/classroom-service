package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceAssignmentView;
import com.timcritt.tfg.application.port.outbound.repository.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;

import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.MaterialReferenceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.util.stream.Collectors;

@Repository
public class MaterialReferenceRepositoryAdapter implements MaterialReferenceRepositoryPort {


    private final MaterialReferenceJpaRepository materialReferenceJpaRepository;

    public MaterialReferenceRepositoryAdapter(

            MaterialReferenceJpaRepository materialReferenceJpaRepository
    ) {

        this.materialReferenceJpaRepository = materialReferenceJpaRepository;
    }

    // ***************************** QUERIES *************************************
//    @Override
//    public MaterialReference findById(Long id) {
//        return materialReferenceJpaRepository.findById(id).map(MaterialReferenceEntityMapper::toDomain).orElse(null);
//    }


    @Override
    public List<MaterialReference> findByClassroomId(Long classroomId) {
        return materialReferenceJpaRepository.findByClassroomId(classroomId)
                .stream()
                .map(MaterialReferenceEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialReference> findByClassroomIdAndAssignedToRole(Long classroomId, ClassroomRole role) {
        return materialReferenceJpaRepository.findByClassroomIdAndAssignedToRole(classroomId, role)
                .stream()
                .map(MaterialReferenceEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialReferenceAssignmentView> findAssignmentsByMaterialId(Long materialId) {
        return materialReferenceJpaRepository.findByMaterialId(materialId).stream()
                .map(entity -> new MaterialReferenceAssignmentView(
                        entity.getClassroom() != null ? entity.getClassroom().getId() : null,
                        entity.getMaterialId(),
                        entity.getAssignedToRole()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialReference> findByMaterialId(Long id) {
        return materialReferenceJpaRepository.findByMaterialId(id).stream().map(MaterialReferenceEntityMapper::toDomain).collect(Collectors.toList());

    }

    // ************************************** COMMANDS *****************************************************
    @Override
    @Transactional
    public int deleteByMaterialId(Long materialId) {
        if (materialId == null) {
            return 0;
        }
        return materialReferenceJpaRepository.deleteByMaterialId(materialId);
    }

    public void save(MaterialReference materialReference) {
        MaterialReferenceJpaEntity materialReferenceJpaEntity = MaterialReferenceEntityMapper.toEntity(materialReference);
        materialReferenceJpaRepository.save(materialReferenceJpaEntity);
    }



    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
