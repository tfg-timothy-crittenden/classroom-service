package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;
import com.timcritt.tfg.domain.model.Member;

import java.util.List;
import java.util.Optional;

public interface ClassroomManagementUseCase {

    // ***************************** QUERIES *************************************

    Classroom assignTeacherToClassroom(Long classroomId, Long userId, String name, String surname);

    Classroom save(Classroom classroom);

    List<MaterialReference> getAllClassroomMaterials(Long classroomId);

    List<MaterialReference> getClassroomMaterialsByRole(Long classroomId, ClassroomRole role);

    List<Member> getMembersByRole(Long classroomId, ClassroomRole role);

    Classroom syncTeachersForClassroom(Long classroomId, List<Member> teachers);


    // ************************************** COMMANDS ***************************************
    Classroom joinClassroom(Long userId, String classCode, String name, String surname);

    void removeMemberFromClassroom(Long classroomId, Long userId);

    int revokeTeacherRoleFromUser(Long userId);

    void replaceMaterials(UpdateClassroomMaterialsCommand command);
}
