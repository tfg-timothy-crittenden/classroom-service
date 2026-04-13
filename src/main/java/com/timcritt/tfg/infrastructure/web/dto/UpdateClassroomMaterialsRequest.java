package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.model.ClassroomRole;
import lombok.Data;

import java.util.List;

@Data
public class UpdateClassroomMaterialsRequest {

    /**
     * The desired final state of the classroom's assigned materials.
     * The service will add missing entries and remove entries not present.
     */
    private List<MaterialAssignmentDto> materials;

    @Data
    public static class MaterialAssignmentDto {
        private Long materialId;
        private String name;
        private String description;
        private ClassroomRole assignedToRole;
    }
}

