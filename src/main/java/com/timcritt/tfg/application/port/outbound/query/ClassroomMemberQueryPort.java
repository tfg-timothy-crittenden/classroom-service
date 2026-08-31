package com.timcritt.tfg.application.port.outbound.query;

import com.timcritt.tfg.application.query.ClassroomMemberView;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

import java.util.List;

public interface ClassroomMemberQueryPort {

    List<ClassroomMemberView> findByClassroomIdAndRole(
            Long classroomId,
            ClassroomRole role
    );
}