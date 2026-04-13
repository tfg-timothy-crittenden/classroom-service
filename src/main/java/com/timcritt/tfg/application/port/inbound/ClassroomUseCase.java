package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.domain.model.Classroom;

import java.util.List;

public interface ClassroomUseCase {
    List<Classroom> getClassroomsByMember(Long userId);

    List<Classroom> getAllClassrooms();
}
