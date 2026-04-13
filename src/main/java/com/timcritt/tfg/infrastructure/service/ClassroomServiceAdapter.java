package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.inbound.ClassroomUseCase;
import com.timcritt.tfg.application.port.outbound.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.service.ClassroomUseCaseImpl;
import com.timcritt.tfg.application.service.MaterialReferenceQueryService;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.MaterialReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassroomServiceAdapter implements ClassroomUseCase {

    private final ClassroomUseCaseImpl delegate;
    private final MaterialReferenceQueryService materialReferenceQueryService;

    public ClassroomServiceAdapter(ClassroomRepositoryPort repository, MaterialReferenceRepositoryPort materialReferenceRepository) {
        this.delegate = new ClassroomUseCaseImpl(repository);
        this.materialReferenceQueryService = new MaterialReferenceQueryService(materialReferenceRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByMember(Long userId) {
        return delegate.getClassroomsByMember(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Classroom> getAllClassrooms() {
        return delegate.getAllClassrooms();
    }

    @Transactional(readOnly = true)
    public List<MaterialReference> getMaterialsByClassroom(Long classroomId) {
        return materialReferenceQueryService.getMaterialsByClassroom(classroomId);
    }
}
