package com.timcritt.tfg.application.port.outbound.repository;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceAssignmentView;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;
import java.util.List;

public interface MaterialReferenceRepositoryPort {

    // ***************************** QUERIES *************************************
    List<MaterialReference> findByClassroomId(Long classroomId);

    List<MaterialReference> findByClassroomIdAndAssignedToRole(Long classroomId, ClassroomRole role);

    List<MaterialReferenceAssignmentView> findAssignmentsByMaterialId(Long materialId);

    List<MaterialReference> findByMaterialId(Long id);


    // ************************************** COMMANDS *****************************************************
    int deleteByMaterialId(Long materialId);

    void save(MaterialReference materialReference);





}
