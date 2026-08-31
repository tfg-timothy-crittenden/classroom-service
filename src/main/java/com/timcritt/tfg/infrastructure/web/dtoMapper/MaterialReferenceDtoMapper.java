package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.aggregate.classroom.MaterialReference;
import com.timcritt.tfg.infrastructure.web.dto.MaterialReferenceDto;

public final class MaterialReferenceDtoMapper {
    private MaterialReferenceDtoMapper() {}

    public static MaterialReferenceDto toDto(MaterialReference materialReference) {
        if (materialReference == null) return null;
        MaterialReferenceDto dto = new MaterialReferenceDto();
        dto.setMaterialId(materialReference.getMaterialId());
        return dto;
    }
}
