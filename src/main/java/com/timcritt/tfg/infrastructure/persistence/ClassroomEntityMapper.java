package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.domain.aggregate.classroom.MaterialReference;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MembershipJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;

import java.util.ArrayList;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ClassroomEntityMapper {
    private ClassroomEntityMapper() {}

    public static Classroom toDomain(ClassroomJpaEntity entity) {
        return toDomain(entity, MembershipEntityMapper::toDomain, MaterialReferenceEntityMapper::toDomain);
    }

    public static Classroom toDomain(
            ClassroomJpaEntity entity,
            java.util.function.Function<MembershipJpaEntity, Membership> memberMapper,
            java.util.function.Function<MaterialReferenceJpaEntity, MaterialReference> materialMapper
    ) {
        if (entity == null) {
            return null;
        }
        Classroom domain = new Classroom(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getJoinCode()
        );
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());


        if (entity.getMembers() != null) {
            Map<Long, Membership> membersMap = distinctByKey(entity.getMembers(), MembershipJpaEntity::getUserId)
                    .stream()
                    .map(memberMapper)
                    .collect(Collectors.toMap(
                            Membership::getUserId,
                            m -> m,
                            (a, b) -> a,
                            java.util.LinkedHashMap::new));
            domain.setMembers(membersMap);
        }

        if (entity.getMaterials() != null) {
            domain.setMaterials(distinctByKey(entity.getMaterials(), MaterialReferenceJpaEntity::getMaterialId)
                    .stream()
                    .map(materialMapper)
                    .collect(Collectors.toList()));
        }

        return domain;
    }

    public static ClassroomJpaEntity toEntity(Classroom domain) {
        return toEntity(domain, MembershipEntityMapper::toEntity, MaterialReferenceEntityMapper::toEntity);
    }

    public static ClassroomJpaEntity toEntity(
            Classroom domain,
            java.util.function.Function<Membership, MembershipJpaEntity> memberMapper,
            java.util.function.Function<MaterialReference, com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity> materialMapper
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
        entity.setJoinCode(domain.getJoinCode());
        if (domain.getMembers() != null) {
            List<MembershipJpaEntity> memberEntities = domain.getMembers().values().stream()
                .map(memberMapper)
                .collect(Collectors.toList());
            memberEntities.forEach(member -> member.setClassroom(entity));
            entity.setMembers(memberEntities);
        }

        if (domain.getMaterials() != null) {
            entity.setMaterials(distinctByKey(domain.getMaterials(), MaterialReference::getMaterialId).stream()
                    .map(material -> {
                        MaterialReferenceJpaEntity materialEntity = materialMapper.apply(material);
                        materialEntity.setClassroom(entity);
                        return materialEntity;
                    })
                    .collect(Collectors.toSet()));
        }

        return entity;
    }

    private static <T, K> List<T> distinctByKey(Collection<T> items, Function<T, K> keyExtractor) {
        Map<K, T> uniqueItems = new LinkedHashMap<>();
        for (T item : items) {
            uniqueItems.putIfAbsent(keyExtractor.apply(item), item);
        }
        return new ArrayList<>(uniqueItems.values());
    }
}
