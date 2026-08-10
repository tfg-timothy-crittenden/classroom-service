package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTeacherRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String teacherName;
    @NotBlank
    private String teacherSurname;



}

