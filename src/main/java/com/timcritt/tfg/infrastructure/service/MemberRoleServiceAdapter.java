package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.application.service.MemberRoleQueryService;
import com.timcritt.tfg.domain.model.ClassroomRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberRoleServiceAdapter {

    private final MemberRoleQueryService delegate;

    public MemberRoleServiceAdapter(MemberRepositoryPort memberRepositoryPort) {
        this.delegate = new MemberRoleQueryService(memberRepositoryPort);
    }

    @Transactional(readOnly = true)
    public Optional<ClassroomRole> getRoleInClassroom(Long classroomId, Long userId) {
        return delegate.getRoleInClassroom(classroomId, userId);
    }
}

