package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;
import java.util.List;

@Data
public class SyncTeachersRequest {
    /**
     * The desired final set of teacher user IDs for the classroom.
     */
    private List<Long> teacherUserIds;
}

