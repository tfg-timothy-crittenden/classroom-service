package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;

@Data
public class MaterialAccessCheckResponse {
    private boolean allowed;
    private String reason;
    private String effectiveRole;
}

