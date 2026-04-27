
package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;
import java.util.List;

@Data
public class SyncTeachersRequest {
    private List<TeacherDto> teachers;
}
