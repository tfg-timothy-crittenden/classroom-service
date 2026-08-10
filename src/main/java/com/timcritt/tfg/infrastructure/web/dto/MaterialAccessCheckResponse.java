package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialAccessCheckResponse {
    @NotNull
    private boolean allowed;
    @NotEmpty
    private String reason;
    @NotEmpty
    private String effectiveRole;
}

