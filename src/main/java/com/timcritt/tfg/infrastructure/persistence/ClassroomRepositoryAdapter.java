package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.domain.model.Classroom;
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
        return classroomJpaRepository.findByJoinCodeWithMembersAndMaterials(joinCode)
                .map(ClassroomEntityMapper::toDomain)
                .orElse( null);
    }

    @Override
    public Classroom findById(Long id) {
        return classroomJpaRepository.findByIdWithMembersAndMaterials(id)
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


}
