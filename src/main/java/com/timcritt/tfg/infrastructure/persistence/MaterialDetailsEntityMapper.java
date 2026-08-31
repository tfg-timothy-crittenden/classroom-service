package com.timcritt.tfg.infrastructure.persistence;


import com.timcritt.tfg.domain.model.MaterialDetails;

import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialDetailsJpaEntity;


public final class MaterialDetailsEntityMapper {

    public static MaterialDetails toDomain(MaterialDetailsJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return  MaterialDetails.builder()
                .materialId(entity.getMaterialId())
                .version(entity.getVersion())
                .name(entity.getName())
                .description(entity.getDescription())
                .part1Title(entity.getPart1Title())
                .part2Title(entity.getPart2Title())
                .build();
    }

    public static MaterialDetailsJpaEntity toJpa(MaterialDetails domain) {
        if (domain == null) {
            return null;
        }

        MaterialDetailsJpaEntity entity = new MaterialDetailsJpaEntity();

        entity.setMaterialId(domain.getMaterialId());
        entity.setVersion(domain.getVersion() == null ? 0L : domain.getVersion());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPart1Title(domain.getPart1Title());
        entity.setPart2Title(domain.getPart2Title());
        return entity;

    }

}
