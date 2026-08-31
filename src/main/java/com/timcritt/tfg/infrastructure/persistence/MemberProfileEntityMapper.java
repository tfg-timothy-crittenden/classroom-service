package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.projection.MemberProfile;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberProfileJpaEntity;

public class MemberProfileEntityMapper {

    private MemberProfileEntityMapper() {}

    public static MemberProfile toDomain(MemberProfileJpaEntity entity) {

        if (entity == null) return null;

        return MemberProfile.builder()
                .userId(entity.getUserId())
                .version(entity.getVersion())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
    }

    public static MemberProfileJpaEntity toEntity(MemberProfile domain) {

        if (domain == null) return null;

        MemberProfileJpaEntity entity = new MemberProfileJpaEntity();
        entity.setUserId(domain.getUserId());
        entity.setVersion(domain.getVersion());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        return entity;
    }

}
