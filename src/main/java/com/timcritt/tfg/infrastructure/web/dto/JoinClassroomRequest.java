package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;

@Data
public class JoinClassroomRequest {
    private Long userId;
    private String classCode;
}

