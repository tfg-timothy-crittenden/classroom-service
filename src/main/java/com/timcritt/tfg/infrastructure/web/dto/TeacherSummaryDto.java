package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;

@Data
public class TeacherSummaryDto {
    private Long memberId;
    private Long userId;
    private String name;
    private String surname;
}

