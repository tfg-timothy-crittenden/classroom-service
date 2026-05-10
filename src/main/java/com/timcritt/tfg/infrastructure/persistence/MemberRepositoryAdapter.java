package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.spring.MemberJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepositoryAdapter implements MemberRepositoryPort {

    private final MemberJpaRepository jpaRepository;

    public MemberRepositoryAdapter(MemberJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ClassroomRole> findRoleByClassroomIdAndUserId(Long classroomId, Long userId) {
        return jpaRepository.findRoleByClassroomIdAndUserId(classroomId, userId);
    }

    @Override
    public int deleteTeacherMembershipsByUserId(Long userId) {
        return jpaRepository.deleteByUserIdAndRole(userId, ClassroomRole.TEACHER);
    }
}

