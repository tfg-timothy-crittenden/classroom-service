package com.timcritt.tfg.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class DeleteClassroomsRequest {
    @NotEmpty
    private List<Long> classroomIds;
}

