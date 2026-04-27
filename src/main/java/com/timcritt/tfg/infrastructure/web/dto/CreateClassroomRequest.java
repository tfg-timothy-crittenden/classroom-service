package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;

@Data
public class CreateClassroomRequest {

    private String name;
    private String description;
}
