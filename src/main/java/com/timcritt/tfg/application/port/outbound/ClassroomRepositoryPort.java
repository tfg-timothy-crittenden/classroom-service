package com.timcritt.tfg.application.port.outbound;

import com.timcritt.tfg.domain.model.Classroom;

import java.util.List;

public interface ClassroomRepositoryPort {

    Classroom save(Classroom classroom);
    Classroom findById(Long id);
    Classroom deleteById(Long id);
    List<Classroom> findByMemberUserId(Long userId);
    List<Classroom> findAll();
}
