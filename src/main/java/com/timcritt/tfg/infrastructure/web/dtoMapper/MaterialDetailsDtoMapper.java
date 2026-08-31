package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.model.MaterialDetails;
import com.timcritt.tfg.infrastructure.web.dto.MaterialDetailsDto;

public final class MaterialDetailsDtoMapper {
    private MaterialDetailsDtoMapper() {}

    public static MaterialDetailsDto toDto(MaterialDetails materialDetails) {
        if (materialDetails == null) {
            return null;
        }

        MaterialDetailsDto dto = new MaterialDetailsDto();
        dto.setMaterialId(materialDetails.getMaterialId());
        dto.setName(materialDetails.getName());
        dto.setDescription(materialDetails.getDescription());
        dto.setPart1Title(materialDetails.getPart1Title());
        dto.setPart2Title(materialDetails.getPart2Title());
        return dto;
    }
}

