package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.projection.MaterialDetails;
import com.timcritt.tfg.domain.aggregate.classroom.MaterialReference;
import com.timcritt.tfg.infrastructure.web.dto.MaterialReferenceWithDetailsDto;

public final class MaterialReferenceWithDetailsDtoMapper {
    private MaterialReferenceWithDetailsDtoMapper() {}

    public static MaterialReferenceWithDetailsDto toDto(MaterialReference materialReference, MaterialDetails materialDetails) {
        if (materialReference == null) {
            return null;
        }

        MaterialReferenceWithDetailsDto dto = new MaterialReferenceWithDetailsDto();
        dto.setMaterialId(materialReference.getMaterialId());
        dto.setAssignedToRole(materialReference.getAssignedToRole());
        if (materialDetails != null) {
            dto.setName(materialDetails.getName());
            dto.setDescription(materialDetails.getDescription());
            dto.setPart1Title(materialDetails.getPart1Title());
            dto.setPart2Title(materialDetails.getPart2Title());
        }
        return dto;
    }
}

