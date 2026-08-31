package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.domain.model.Classroom;

import java.util.List;

public interface ClassroomDirectoryUseCase {

    // ***************************** QUERIES *************************************
    List<Classroom> getClassroomsByMember(Long userId);
    List<Classroom> getAllClassrooms();
    Classroom getClassroomById(Long classroomId);

    // ************************************** COMMANDS *****************************************************
    void deleteClassroomById(Long classroomId);
    void deleteClassroomsByIds(List<Long> classroomIds);
}
