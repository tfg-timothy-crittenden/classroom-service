package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MembershipJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ClassroomRepositoryAdapter implements ClassroomRepositoryPort {

    private final ClassroomJpaRepository classroomJpaRepository;

    public ClassroomRepositoryAdapter(ClassroomJpaRepository classroomJpaRepository) {
        this.classroomJpaRepository = classroomJpaRepository;
    }

    @Override
    public List<Classroom> findByMemberUserId(Long userId) {
        return classroomJpaRepository.findByMemberUserId(userId).stream()
                .map(ClassroomEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Classroom> findAll() {
        return classroomJpaRepository.findAll().stream()
                .map(ClassroomEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Classroom findByJoinCode(String joinCode) {
        return classroomJpaRepository.findByJoinCodeWithMembersAndMaterials(joinCode)
                .map(ClassroomEntityMapper::toDomain)
                .orElse( null);
    }

    @Override
    public Classroom findById(Long id) {
        return classroomJpaRepository.findByIdWithMembersAndMaterials(id)
                .map(ClassroomEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Classroom save(Classroom classroom) {
        ClassroomJpaEntity mapped = ClassroomEntityMapper.toEntity(classroom);

        if (classroom.getId() == null) {
            return ClassroomEntityMapper.toDomain(classroomJpaRepository.save(mapped));
        }

        ClassroomJpaEntity entity = classroomJpaRepository.findByIdWithMembersAndMaterials(classroom.getId())
                .orElse(mapped);

        entity.setName(mapped.getName());
        entity.setDescription(mapped.getDescription());
        entity.setCreatedAt(mapped.getCreatedAt());
        entity.setUpdatedAt(mapped.getUpdatedAt());
        entity.setJoinCode(mapped.getJoinCode());

        syncMembers(entity, mapped);
        syncMaterials(entity, mapped);

        return ClassroomEntityMapper.toDomain(classroomJpaRepository.save(entity));
    }

    private static void syncMembers(ClassroomJpaEntity entity, ClassroomJpaEntity mapped) {
        Map<Long, MembershipJpaEntity> existingByUserId = new HashMap<>();
        for (MembershipJpaEntity existing : entity.getMembers()) {
            existingByUserId.put(existing.getUserId(), existing);
        }

        Set<Long> desiredUserIds = new HashSet<>();
        if (mapped.getMembers() != null) {
            for (MembershipJpaEntity desired : mapped.getMembers()) {
                desiredUserIds.add(desired.getUserId());
                MembershipJpaEntity current = existingByUserId.get(desired.getUserId());
                if (current == null) {
                    MembershipJpaEntity created = new MembershipJpaEntity();
                    created.setUserId(desired.getUserId());
                    created.setRole(desired.getRole());
                    created.setCreatedAt(desired.getCreatedAt());
                    created.setUpdatedAt(desired.getUpdatedAt());
                    entity.addMember(created);
                } else {
                    current.setRole(desired.getRole());
                    current.setCreatedAt(desired.getCreatedAt());
                    current.setUpdatedAt(desired.getUpdatedAt());
                }
            }
        }

        entity.getMembers().removeIf(member -> !desiredUserIds.contains(member.getUserId()));
    }

    private static void syncMaterials(ClassroomJpaEntity entity, ClassroomJpaEntity mapped) {
        Map<Long, MaterialReferenceJpaEntity> existingByMaterialId = new HashMap<>();
        for (MaterialReferenceJpaEntity existing : entity.getMaterials()) {
            existingByMaterialId.put(existing.getMaterialId(), existing);
        }

        Set<Long> desiredMaterialIds = new HashSet<>();
        if (mapped.getMaterials() != null) {
            for (MaterialReferenceJpaEntity desired : mapped.getMaterials()) {
                desiredMaterialIds.add(desired.getMaterialId());
                MaterialReferenceJpaEntity current = existingByMaterialId.get(desired.getMaterialId());
                if (current == null) {
                    MaterialReferenceJpaEntity created = new MaterialReferenceJpaEntity();
                    created.setMaterialId(desired.getMaterialId());
                    created.setAssignedToRole(desired.getAssignedToRole());
                    entity.addMaterial(created);
                } else {
                    current.setAssignedToRole(desired.getAssignedToRole());
                }
            }
        }

        entity.getMaterials().removeIf(material -> !desiredMaterialIds.contains(material.getMaterialId()));
    }

    @Override
    public void deleteById(Long id) {
        classroomJpaRepository.findById(id).ifPresent(entity -> classroomJpaRepository.deleteById(id));
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        classroomJpaRepository.deleteAllById(ids);
    }


}
