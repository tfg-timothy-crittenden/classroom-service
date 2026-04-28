package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.ClassroomRepositoryPort;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ClassroomRepositoryAdapter implements ClassroomRepositoryPort {

    private final ClassroomJpaRepository classroomJpaRepository;

    public ClassroomRepositoryAdapter(ClassroomJpaRepository classroomJpaRepository) {
        this.classroomJpaRepository = classroomJpaRepository;
    }

    @Override
    public List<Classroom> findByMemberUserId(Long userId) {
        return classroomJpaRepository.findByMemberUserId(userId).stream()
                .map(ClassroomEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Classroom> findAll() {
        return classroomJpaRepository.findAll().stream()
                .map(ClassroomEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Classroom findByJoinCode(String joinCode) {
        return classroomJpaRepository.findByJoinCode(joinCode)
                .map(ClassroomEntityMapper::toDomain)
                .orElse( null);
    }

    @Override
    public Classroom findById(Long id) {
        // Fetch only members to avoid MultipleBagFetchException
        return classroomJpaRepository.findByIdWithMembers(id)
                .map(ClassroomEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Classroom save(Classroom classroom) {
        ClassroomJpaEntity entity = ClassroomEntityMapper.toEntity(classroom);
        return ClassroomEntityMapper.toDomain(classroomJpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        classroomJpaRepository.findById(id).ifPresent(entity -> classroomJpaRepository.deleteById(id));
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        classroomJpaRepository.deleteAllById(ids);
    }

    @Override
    public boolean removeMemberFromClassroom(Long classroomId, Long userId) {
        ClassroomJpaEntity entity = classroomJpaRepository.findByIdWithMembersAndMaterials(classroomId).orElse(null);
        if (entity == null || entity.getMembers() == null) {
            return false;
        }

        MemberJpaEntity memberToRemove = entity.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (memberToRemove == null) {
            return false;
        }

        entity.removeMember(memberToRemove);
        classroomJpaRepository.save(entity);
        return true;
    }
}
