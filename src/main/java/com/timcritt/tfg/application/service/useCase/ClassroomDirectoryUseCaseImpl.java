package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.port.inbound.ClassroomDirectoryUseCase;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;

import java.util.List;

public class ClassroomDirectoryUseCaseImpl implements ClassroomDirectoryUseCase {

    private final ClassroomRepositoryPort classroomRepository;

    public ClassroomDirectoryUseCaseImpl(ClassroomRepositoryPort repository) {
        this.classroomRepository = repository;
    }

    // ***************************** QUERIES *************************************

    @Override
    public Classroom getClassroomById(Long classroomId) {
        return classroomRepository.findById(classroomId);
    }

    // Queries
    @Override
    public List<Classroom> getClassroomsByMember(Long userId) {
        return classroomRepository.findByMemberUserId(userId);
    }

    @Override
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    // ************************************** COMMANDS *****************************************************

    @Override
    public void deleteClassroomById(Long classroomId) {
        classroomRepository.deleteById(classroomId);
    }

    @Override
    public void deleteClassroomsByIds(List<Long> classroomIds) {
        classroomRepository.deleteByIds(classroomIds);
    }
}
