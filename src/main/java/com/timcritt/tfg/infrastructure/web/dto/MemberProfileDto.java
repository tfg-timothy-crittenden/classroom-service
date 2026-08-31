package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class MemberProfileDto {

    @NotBlank
    private long userId;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
}
