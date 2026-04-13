package com.timcritt.tfg.application.port.outbound;
import com.timcritt.tfg.domain.model.MaterialReference;
import java.util.List;
public interface MaterialReferenceCommandPort {
    List<MaterialReference> upsertForClassroom(Long classroomId, List<MaterialReference> desired);
}
