package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.application.service.useCase.ClassroomDirectoryUseCaseImpl;
import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassroomDirectoryAdapter  {

    private final ClassroomDirectoryUseCaseImpl delegate;

    public ClassroomDirectoryAdapter(ClassroomRepositoryPort repository) {
        this.delegate = new ClassroomDirectoryUseCaseImpl(repository);
    }

    // ***************************** QUERIES *************************************
    @Transactional
    public Classroom getClassroomById(Long classroomId) {
        return delegate.getClassroomById(classroomId);
    }

    @Transactional
    public List<Classroom> getClassroomsByMember(Long userId) {
        return delegate.getClassroomsByMember(userId);
    }

    @Transactional
    public List<Classroom> getAllClassrooms() {
        return delegate.getAllClassrooms();
    }

    // ************************************** COMMANDS *****************************************************
    @Transactional
    public void deleteClassroomById(Long classroomId) {
        delegate.deleteClassroomById(classroomId);
    }

    @Transactional
    public void deleteClassroomsByIds(List<Long> classroomIds) {
        delegate.deleteClassroomsByIds(classroomIds);
    }

}
