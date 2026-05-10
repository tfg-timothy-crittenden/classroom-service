package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.exception.ClassroomNotFoundException;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.domain.model.MaterialReference;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import com.timcritt.tfg.infrastructure.persistence.spring.MaterialReferenceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class MaterialReferenceCommandAdapter implements MaterialReferenceCommandPort {

    private final ClassroomJpaRepository classroomJpaRepository;
    private final MaterialReferenceJpaRepository materialReferenceJpaRepository;

    public MaterialReferenceCommandAdapter(
            ClassroomJpaRepository classroomJpaRepository,
            MaterialReferenceJpaRepository materialReferenceJpaRepository
    ) {
        this.classroomJpaRepository = classroomJpaRepository;
        this.materialReferenceJpaRepository = materialReferenceJpaRepository;
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
                existing.setPart1Title(desiredRef.getPart1Title());
                existing.setPart2Title(desiredRef.getPart2Title());
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

    @Override
    @Transactional
    public int deleteByMaterialId(Long materialId) {
        if (materialId == null) {
            return 0;
        }
        return materialReferenceJpaRepository.deleteByMaterialId(materialId);
    }

    @Override
    @Transactional
    public int updateTitlesByMaterialId(Long materialId, String title, String part1Title, String part2Title) {
        String normalizedTitle = normalize(title);
        String normalizedPart1Title = normalize(part1Title);
        String normalizedPart2Title = normalize(part2Title);

        if (materialId == null || (normalizedTitle == null && normalizedPart1Title == null && normalizedPart2Title == null)) {
            return 0;
        }

        List<MaterialReferenceJpaEntity> references = materialReferenceJpaRepository.findByMaterialId(materialId);
        if (references.isEmpty()) {
            return 0;
        }

        for (MaterialReferenceJpaEntity reference : references) {
            if (normalizedTitle != null) {
                reference.setName(normalizedTitle);
            }
            if (normalizedPart1Title != null) {
                reference.setPart1Title(normalizedPart1Title);
            }
            if (normalizedPart2Title != null) {
                reference.setPart2Title(normalizedPart2Title);
            }
        }

        materialReferenceJpaRepository.saveAll(references);
        return references.size();
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
