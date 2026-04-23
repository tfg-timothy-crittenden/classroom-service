package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeleteClassroomsRequest {
    private List<Long> classroomIds;
}

