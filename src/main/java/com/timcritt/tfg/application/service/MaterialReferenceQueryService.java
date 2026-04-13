package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;

import java.util.List;

public class MaterialReferenceQueryService {
    private final MaterialReferenceRepositoryPort repository;

    public MaterialReferenceQueryService(MaterialReferenceRepositoryPort repository) {
        this.repository = repository;
    }

    public List<MaterialReference> getMaterialsByClassroom(Long classroomId) {
        return repository.findByClassroomId(classroomId);
    }

    public List<MaterialReference> getMaterialsByClassroomAndRole(Long classroomId, ClassroomRole role) {
        return repository.findByClassroomIdAndAssignedToRole(classroomId, role);
    }
}

