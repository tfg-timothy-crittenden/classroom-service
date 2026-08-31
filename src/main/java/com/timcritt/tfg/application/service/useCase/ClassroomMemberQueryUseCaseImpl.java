package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.port.inbound.ClassroomMemberQueryUseCase;
import com.timcritt.tfg.application.port.outbound.query.ClassroomMemberQueryPort;
import com.timcritt.tfg.application.query.ClassroomMemberView;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

import java.util.List;
import java.util.Objects;

public class ClassroomMemberQueryUseCaseImpl
        implements ClassroomMemberQueryUseCase {

    private final ClassroomMemberQueryPort classroomMemberQueryPort;

    public ClassroomMemberQueryUseCaseImpl(
            ClassroomMemberQueryPort classroomMemberQueryPort
    ) {
        this.classroomMemberQueryPort = classroomMemberQueryPort;
    }

    @Override
    public List<ClassroomMemberView> getMembersByRole(
            Long classroomId,
            ClassroomRole role
    ) {
        Objects.requireNonNull(classroomId, "classroomId cannot be null");
        Objects.requireNonNull(role, "role cannot be null");

        return classroomMemberQueryPort.findByClassroomIdAndRole(
                classroomId,
                role
        );
    }
}