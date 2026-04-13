package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;

import java.time.Instant;
import java.util.stream.Collectors;

public final class ClassroomEntityMapper {
    private ClassroomEntityMapper() {}

    public static Classroom toDomain(ClassroomJpaEntity entity) {
        return toDomain(entity, MemberEntityMapper::toDomain, MaterialReferenceEntityMapper::toDomain);
    }

    public static Classroom toDomain(
            ClassroomJpaEntity entity,
            java.util.function.Function<com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity, com.timcritt.tfg.domain.model.Member> memberMapper,
            java.util.function.Function<com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity, com.timcritt.tfg.domain.model.MaterialReference> materialMapper
    ) {
        if (entity == null) {
            return null;
        }
        Classroom domain = new Classroom(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getMembers() != null) {
            domain.setMembers(entity.getMembers().stream().map(memberMapper).collect(Collectors.toList()));
        }
        if (entity.getMaterials() != null) {
            domain.setMaterials(entity.getMaterials().stream().map(materialMapper).collect(Collectors.toList()));
        }
        return domain;
    }

    public static ClassroomJpaEntity toEntity(Classroom domain) {
        return toEntity(domain, MemberEntityMapper::toEntity, MaterialReferenceEntityMapper::toEntity);
    }

    public static ClassroomJpaEntity toEntity(
            Classroom domain,
            java.util.function.Function<com.timcritt.tfg.domain.model.Member, com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity> memberMapper,
            java.util.function.Function<com.timcritt.tfg.domain.model.MaterialReference, com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity> materialMapper
    ) {
        if (domain == null) {
            return null;
        }

        Instant createdAt = domain.getCreatedAt() != null ? domain.getCreatedAt() : Instant.now();
        Instant updatedAt = domain.getUpdatedAt() != null ? domain.getUpdatedAt() : Instant.now();

        ClassroomJpaEntity entity = new ClassroomJpaEntity(
                domain.getName(),
                domain.getDescription(),
                createdAt,
                updatedAt
        );
        entity.setId(domain.getId());
        if (domain.getMembers() != null) {
            entity.setMembers(domain.getMembers().stream().map(memberMapper).collect(Collectors.toList()));
        }
        if (domain.getMaterials() != null) {
            entity.setMaterials(domain.getMaterials().stream().map(materialMapper).collect(Collectors.toList()));
        }
        return entity;
    }
}
