package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.infrastructure.persistence.jpa.MembershipJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;

public final class MembershipEntityMapper {
    private MembershipEntityMapper() {}

    public static Membership toDomain(MembershipJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Membership(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getSurname(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // Overload toEntity to accept classroom entity
    public static MembershipJpaEntity toEntity(Membership domain, ClassroomJpaEntity classroomEntity) {
        if (domain == null) {
            return null;
        }
        MembershipJpaEntity entity = new MembershipJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setName(domain.getName());
        entity.setSurname(domain.getSurname());
        entity.setRole(domain.getRole());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setClassroom(classroomEntity); // Set classroom reference
        return entity;
    }

    // Keep the old method for compatibility
    public static MembershipJpaEntity toEntity(Membership domain) {
        return toEntity(domain, null);
    }
}
