package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateClassroomRequest {
    @NotEmpty
    private String name;
    //Optional
    private String description;
}
