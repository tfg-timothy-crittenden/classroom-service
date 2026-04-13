package com.timcritt.tfg.application.port.outbound;

import com.timcritt.tfg.domain.model.ClassroomRole;

import java.util.Optional;

public interface MemberRepositoryPort {
    Optional<ClassroomRole> findRoleByClassroomIdAndUserId(Long classroomId, Long userId);
}

