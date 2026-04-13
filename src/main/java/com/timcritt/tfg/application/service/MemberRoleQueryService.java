package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;

import java.util.Optional;

public class MemberRoleQueryService {

    private final MemberRepositoryPort memberRepository;

    public MemberRoleQueryService(MemberRepositoryPort memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Optional<ClassroomRole> getRoleInClassroom(Long classroomId, Long userId) {
        return memberRepository.findRoleByClassroomIdAndUserId(classroomId, userId);
    }
}

