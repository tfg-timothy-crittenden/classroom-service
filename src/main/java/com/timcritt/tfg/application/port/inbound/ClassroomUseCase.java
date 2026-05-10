package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.Member;

import java.util.List;

public interface ClassroomUseCase {
    List<Classroom> getClassroomsByMember(Long userId);

    List<Classroom> getAllClassrooms();

    Classroom assignTeacherToClassroom(Long classroomId, Member member);

    Classroom save(Classroom classroom);

    void deleteClassroomById(Long classroomId);

    void deleteClassroomsByIds(List<Long> classroomIds);
    
    List<Member> getMembersByRole(Long classroomId, ClassroomRole role);
    
    Classroom syncTeachersForClassroom(Long classroomId, List<Member> teachers);

    Classroom getClassroomById(Long classroomId);

    Classroom joinClassroom(Long userId, String classCode, String name, String surname);

    boolean removeMemberFromClassroom(Long classroomId, Long userId);

    int revokeTeacherRoleFromUser(Long userId);
}
