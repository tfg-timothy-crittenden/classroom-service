package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.application.query.ClassroomMemberView;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

import java.util.List;

public interface ClassroomMemberQueryUseCase {

    List<ClassroomMemberView> getMembersByRole(
            Long classroomId,
            ClassroomRole role
    );
}