package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.model.ClassroomRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateClassroomMaterialsRequest {

    /**
     * The desired final state of the classroom's assigned materials.
     * The service will add missing entries and remove entries not present.
     */
    @NotNull
    private List<@Valid MaterialAssignmentDto> materials;

    @Data
    public static class MaterialAssignmentDto {
        @NotNull
        private Long materialId;
        @NotEmpty
        private String name;
        //Optional
        private String description;
        @NotEmpty
        private String part1Title;
        @NotEmpty
        private String part2Title;
        @NotNull
        private ClassroomRole assignedToRole;
    }
}
