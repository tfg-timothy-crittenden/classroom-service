package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity;

public final class MemberEntityMapper {
    private MemberEntityMapper() {}

    public static Member toDomain(MemberJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Member(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getSurname(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static MemberJpaEntity toEntity(Member domain) {
        if (domain == null) {
            return null;
        }
        MemberJpaEntity entity = new MemberJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setName(domain.getName());
        entity.setSurname(domain.getSurname());
        entity.setRole(domain.getRole());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
