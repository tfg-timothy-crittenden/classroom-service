package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SyncTeachersRequest {

    @NotNull
    private List<@Valid TeacherDto> teachers;
}
