package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.model.ClassroomRole;
import lombok.Data;

@Data
public class MemberDto {
    private Long userId;
    private ClassroomRole role;
    private String name;
    private String surname;
}
