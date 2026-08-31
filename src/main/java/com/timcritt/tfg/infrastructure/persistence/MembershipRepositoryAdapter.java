package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.repository.MembershipRepositoryPort;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.infrastructure.persistence.spring.MembershipJpaRepository;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import com.timcritt.tfg.infrastructure.persistence.jpa.MembershipJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Repository
public class MembershipRepositoryAdapter implements MembershipRepositoryPort {

    private final MembershipJpaRepository jpaRepository;
    private final ClassroomJpaRepository classroomJpaRepository;

    public MembershipRepositoryAdapter(MembershipJpaRepository jpaRepository, ClassroomJpaRepository classroomJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.classroomJpaRepository = classroomJpaRepository;
    }

    @Override
    public Optional<ClassroomRole> findRoleByClassroomIdAndUserId(Long classroomId, Long userId) {
        return jpaRepository.findRoleByClassroomIdAndUserId(classroomId, userId);
    }

    @Override
    public int deleteTeacherMembershipsByUserId(Long userId) {
        return jpaRepository.deleteByUserIdAndRole(userId, ClassroomRole.TEACHER);
    }

    @Override
    @Transactional
    public void saveMember(Long classroomId, Membership membership) {
        MembershipJpaEntity entity = MembershipEntityMapper.toEntity(membership, classroomJpaRepository.findById(classroomId)
            .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomId)));
        jpaRepository.save(entity);
    }
}
