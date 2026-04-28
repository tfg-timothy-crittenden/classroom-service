package com.timcritt.tfg.application.port.outbound;

import com.timcritt.tfg.domain.model.Classroom;

import java.util.List;

public interface ClassroomRepositoryPort {

    Classroom save(Classroom classroom);
    Classroom findById(Long id);
    void deleteById(Long id);
    void deleteByIds(List<Long> ids);
    List<Classroom> findByMemberUserId(Long userId);
    List<Classroom> findAll();
    Classroom findByJoinCode(String joinCode);
    boolean removeMemberFromClassroom(Long classroomId, Long userId);
}
