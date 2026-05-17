package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.infrastructure.persistence.spring.MemberJpaRepository;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Repository
public class MemberRepositoryAdapter implements MemberRepositoryPort {

    private final MemberJpaRepository jpaRepository;
    private final ClassroomJpaRepository classroomJpaRepository;

    public MemberRepositoryAdapter(MemberJpaRepository jpaRepository, ClassroomJpaRepository classroomJpaRepository) {
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
    public void saveMember(Long classroomId, Member member) {
        MemberJpaEntity entity = MemberEntityMapper.toEntity(member, classroomJpaRepository.findById(classroomId)
            .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + classroomId)));
        jpaRepository.save(entity);
    }
}
