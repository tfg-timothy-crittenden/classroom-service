package com.timcritt.tfg.application.port.outbound;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.Member;

import java.util.Optional;

public interface MemberRepositoryPort {
    Optional<ClassroomRole> findRoleByClassroomIdAndUserId(Long classroomId, Long userId);
    int deleteTeacherMembershipsByUserId(Long userId);
    void saveMember(Long classroomId, Member member);
}
