package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.exception.ClassroomNotFoundException;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.domain.model.MaterialReference;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class MaterialReferenceCommandAdapter implements MaterialReferenceCommandPort {

    private final ClassroomJpaRepository classroomJpaRepository;

    public MaterialReferenceCommandAdapter(ClassroomJpaRepository classroomJpaRepository) {
        this.classroomJpaRepository = classroomJpaRepository;
    }

    @Override
    @Transactional
    public List<MaterialReference> upsertForClassroom(Long classroomId, List<MaterialReference> desired) {
        List<MaterialReference> safeDesired = desired == null ? List.of() : desired;

        ClassroomJpaEntity classroom = classroomJpaRepository.findByIdWithMaterials(classroomId)
                .orElseThrow(() -> new ClassroomNotFoundException(classroomId));

        // Index desired by materialId (unique per classroom)
        Map<Long, MaterialReference> desiredByMaterialId = safeDesired.stream()
                .filter(m -> m.getMaterialId() != null)
                .collect(Collectors.toMap(
                        MaterialReference::getMaterialId,
                        Function.identity(),
                        (a, b) -> b,
                        LinkedHashMap::new
                ));

        // Current indexed by materialId
        Map<Long, MaterialReferenceJpaEntity> currentByMaterialId = classroom.getMaterials().stream()
                .filter(e -> e.getMaterialId() != null)
                .collect(Collectors.toMap(MaterialReferenceJpaEntity::getMaterialId, Function.identity()));

        // Remove materials no longer desired
        List<MaterialReferenceJpaEntity> toRemove = classroom.getMaterials().stream()
                .filter(e -> !desiredByMaterialId.containsKey(e.getMaterialId()))
                .toList();
        toRemove.forEach(classroom::removeMaterial);

        // Upsert desired
        for (MaterialReference desiredRef : desiredByMaterialId.values()) {
            MaterialReferenceJpaEntity existing = currentByMaterialId.get(desiredRef.getMaterialId());
            if (existing == null) {
                MaterialReferenceJpaEntity created = MaterialReferenceEntityMapper.toEntity(desiredRef);
                classroom.addMaterial(created);
            } else {
                existing.setName(desiredRef.getName());
                existing.setDescription(desiredRef.getDescription());
                existing.setAssignedToRole(desiredRef.getAssignedToRole());
            }
        }

        // Persist via owning side (orphanRemoval will handle deletes)
        ClassroomJpaEntity saved = classroomJpaRepository.save(classroom);

        // ensure entities are persisted; map final state to domain
        return saved.getMaterials().stream()
                .map(MaterialReferenceEntityMapper::toDomain)
                .toList();
    }
}
