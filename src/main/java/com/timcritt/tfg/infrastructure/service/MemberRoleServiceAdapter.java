package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.repository.MemberRepositoryPort;
import com.timcritt.tfg.application.service.useCase.MaterialAccessAuthorizationUseCaseImpl;
import com.timcritt.tfg.domain.model.ClassroomRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberRoleServiceAdapter {

    private final MaterialAccessAuthorizationUseCaseImpl delegate;

    public MemberRoleServiceAdapter(MemberRepositoryPort memberRepositoryPort) {
        this.delegate = new MaterialAccessAuthorizationUseCaseImpl(memberRepositoryPort);
    }

    @Transactional(readOnly = true)
    public Optional<ClassroomRole> getRoleInClassroom(Long classroomId, Long userId) {
        return delegate.getMemberClassroomRole(classroomId, userId);
    }
}

