package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.inbound.ClassroomUseCase;
import com.timcritt.tfg.application.port.outbound.ClassroomRepositoryPort;
import com.timcritt.tfg.domain.model.Classroom;

import java.util.List;

public class ClassroomUseCaseImpl implements ClassroomUseCase {

    private final ClassroomRepositoryPort repository;

    public ClassroomUseCaseImpl(ClassroomRepositoryPort repository) {
        this.repository = repository;

    }

    @Override
    public List<Classroom> getClassroomsByMember(Long userId) {
        return repository.findByMemberUserId(userId);
    }

    @Override
    public List<Classroom> getAllClassrooms() {
        return repository.findAll();
    }
}
