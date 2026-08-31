package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.query.ClassroomMemberQueryPort;
import com.timcritt.tfg.application.query.ClassroomMemberView;
import com.timcritt.tfg.application.service.useCase.ClassroomMemberQueryUseCaseImpl;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassroomMemberQueryAdapter {

    private final ClassroomMemberQueryUseCaseImpl delegate;

    public ClassroomMemberQueryAdapter(
            ClassroomMemberQueryPort classroomMemberQueryPort
    ) {
        this.delegate =
                new ClassroomMemberQueryUseCaseImpl(
                        classroomMemberQueryPort
                );
    }

    @Transactional(readOnly = true)
    public List<ClassroomMemberView> getTeachers(
            Long classroomId
    ) {
        return delegate.getMembersByRole(
                classroomId,
                ClassroomRole.TEACHER
        );
    }

    @Transactional(readOnly = true)
    public List<ClassroomMemberView> getStudents(
            Long classroomId
    ) {
        return delegate.getMembersByRole(
                classroomId,
                ClassroomRole.STUDENT
        );
    }
}