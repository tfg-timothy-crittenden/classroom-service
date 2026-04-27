package com.timcritt.tfg.application.port.outbound;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;
import java.util.List;

public interface MaterialReferenceRepositoryPort {
    List<MaterialReference> findByClassroomId(Long classroomId);

    List<MaterialReference> findByClassroomIdAndAssignedToRole(Long classroomId, ClassroomRole role);

    List<MaterialReferenceAssignmentView> findAssignmentsByMaterialId(Long materialId);
}
