package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.port.inbound.MaterialAccessAuthorizationUseCase;
import com.timcritt.tfg.application.port.outbound.repository.MemberRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;

import java.util.Optional;

public class MaterialAccessAuthorizationUseCaseImpl implements MaterialAccessAuthorizationUseCase {

    private final MemberRepositoryPort memberRepository;

    public MaterialAccessAuthorizationUseCaseImpl(MemberRepositoryPort memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    public Optional<ClassroomRole> getMemberClassroomRole(Long classroomId, Long userId) {
        return memberRepository.findRoleByClassroomIdAndUserId(classroomId, userId);
    }
}

