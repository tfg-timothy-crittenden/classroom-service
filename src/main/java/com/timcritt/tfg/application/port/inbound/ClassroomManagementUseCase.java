package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.domain.aggregate.classroom.MaterialReference;
import com.timcritt.tfg.domain.aggregate.classroom.Membership;

import java.util.List;

public interface ClassroomManagementUseCase {

    // ***************************** QUERIES *************************************


    Classroom save(Classroom classroom);

    List<MaterialReference> getAllClassroomMaterials(Long classroomId);

    List<MaterialReference> getClassroomMaterialsByRole(Long classroomId, ClassroomRole role);

    List<Membership> getMembersByRole(Long classroomId, ClassroomRole role);

    Classroom assignTeacherToClassroom(Long classroomId, Long userId, String name, String surname);

    Classroom syncTeachersForClassroom(Long classroomId, List<Membership> teachers);


    // ************************************** COMMANDS ***************************************
    Classroom joinClassroom(Long userId, String classCode);

    void removeMemberFromClassroom(Long classroomId, Long userId);

    int revokeTeacherRoleFromUser(Long userId);

    void replaceMaterials(UpdateClassroomMaterialsCommand command);
}
