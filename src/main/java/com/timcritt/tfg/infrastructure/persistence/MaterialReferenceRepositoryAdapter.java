package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;
import com.timcritt.tfg.infrastructure.persistence.spring.MaterialReferenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MaterialReferenceRepositoryAdapter implements MaterialReferenceRepositoryPort {
    private final MaterialReferenceJpaRepository materialReferenceJpaRepository;

    public MaterialReferenceRepositoryAdapter(MaterialReferenceJpaRepository materialReferenceJpaRepository) {
        this.materialReferenceJpaRepository = materialReferenceJpaRepository;
    }

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
}
