package com.timcritt.tfg.application.port.outbound.repository;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.domain.aggregate.classroom.Membership;

import java.util.Optional;

public interface MembershipRepositoryPort {
    Optional<ClassroomRole> findRoleByClassroomIdAndUserId(Long classroomId, Long userId);
    int deleteTeacherMembershipsByUserId(Long userId);
    void saveMember(Long classroomId, Membership membership);
}
