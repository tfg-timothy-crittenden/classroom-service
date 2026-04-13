package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.service.MaterialReferenceQueryService;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaterialReferenceServiceAdapter {

    private final MaterialReferenceQueryService delegate;

    public MaterialReferenceServiceAdapter(MaterialReferenceRepositoryPort repositoryPort) {
        this.delegate = new MaterialReferenceQueryService(repositoryPort);
    }

    @Transactional(readOnly = true)
    public List<MaterialReference> getMaterialsByClassroom(Long classroomId) {
        return delegate.getMaterialsByClassroom(classroomId);
    }

    @Transactional(readOnly = true)
    public List<MaterialReference> getMaterialsByClassroomAndRole(Long classroomId, ClassroomRole role) {
        return delegate.getMaterialsByClassroomAndRole(classroomId, role);
    }
}
