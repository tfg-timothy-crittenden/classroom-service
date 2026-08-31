package com.timcritt.tfg.infrastructure.web.dto;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembershipWithMemberProfile {
    @NotNull
    private Long userId;
    @NotNull
    private ClassroomRole role;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
