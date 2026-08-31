package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.port.inbound.MaterialAccessAuthorizationUseCase;
import com.timcritt.tfg.application.port.outbound.repository.MembershipRepositoryPort;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

import java.util.Optional;

public class MaterialAccessAuthorizationUseCaseImpl implements MaterialAccessAuthorizationUseCase {

    private final MembershipRepositoryPort memberRepository;

    public MaterialAccessAuthorizationUseCaseImpl(MembershipRepositoryPort memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    public Optional<ClassroomRole> getMemberClassroomRole(Long classroomId, Long userId) {
        return memberRepository.findRoleByClassroomIdAndUserId(classroomId, userId);
    }
}

