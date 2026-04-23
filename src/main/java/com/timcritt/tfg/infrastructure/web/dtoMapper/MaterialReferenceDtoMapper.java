package com.timcritt.tfg.infrastructure.web;

import com.timcritt.tfg.domain.model.MaterialReference;
import com.timcritt.tfg.infrastructure.web.dto.MaterialReferenceDto;

public final class MaterialReferenceDtoMapper {
    private MaterialReferenceDtoMapper() {}

    public static MaterialReferenceDto toDto(MaterialReference materialReference) {
        if (materialReference == null) return null;
        MaterialReferenceDto dto = new MaterialReferenceDto();
        dto.setMaterialId(materialReference.getMaterialId());
        dto.setName(materialReference.getName());
        dto.setDescription(materialReference.getDescription());
        return dto;
    }
}
