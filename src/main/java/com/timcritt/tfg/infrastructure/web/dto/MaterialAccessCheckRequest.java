package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialAccessCheckRequest {
    @NotBlank
    private String userId;

    @NotNull
    private Long materialId;

    @NotBlank
    private String action;
}

