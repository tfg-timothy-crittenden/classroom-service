package com.timcritt.tfg.application.port.outbound.repository;

import com.timcritt.tfg.domain.model.Classroom;

import java.util.List;

public interface ClassroomRepositoryPort {

    // Commands
    Classroom save(Classroom classroom);
    void deleteById(Long id);
    void deleteByIds(List<Long> ids);

    // Queries
    Classroom findById(Long id);
    List<Classroom> findByMemberUserId(Long userId);
    List<Classroom> findAll();
    Classroom findByJoinCode(String joinCode);

}
