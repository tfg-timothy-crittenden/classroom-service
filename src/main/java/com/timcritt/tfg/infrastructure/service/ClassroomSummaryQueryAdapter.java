package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.query.ClassroomMemberQueryPort;
import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.application.query.ClassroomSummaryView;
import com.timcritt.tfg.application.service.useCase.ClassroomSummaryQueryUseCaseImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassroomSummaryQueryAdapter {

    private final ClassroomSummaryQueryUseCaseImpl delegate;

    public ClassroomSummaryQueryAdapter(ClassroomRepositoryPort classroomRepository,
                                        ClassroomMemberQueryPort classroomMemberQueryPort) {
        this.delegate = new ClassroomSummaryQueryUseCaseImpl(classroomRepository, classroomMemberQueryPort);
    }

    @Transactional(readOnly = true)
    public List<ClassroomSummaryView> getClassroomSummariesByMember(Long userId) {
        return delegate.getClassroomSummariesByMember(userId);
    }

    @Transactional(readOnly = true)
    public List<ClassroomSummaryView> getAllClassroomSummaries() {
        return delegate.getAllClassroomSummaries();
    }
}
