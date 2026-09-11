package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.infrastructure.web.dto.UpdateClassroomMaterialsRequest;
import com.timcritt.tfg.infrastructure.web.dto.TeacherDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.cloud.config.enabled=false",
        "classroom.grpc.enabled=false"
})
class ClassroomMaterialsReplaceIntegrationTest {

    @Autowired
    private ClassroomManagementAdapter classroomManagementAdapter;

    @Test
    void replaceMaterials_allowsOverlappingMaterialIdsAndReplacesRows() {
        Classroom saved = classroomManagementAdapter.save(new Classroom(null, "replace-materials", "desc"));

        UpdateClassroomMaterialsRequest first = new UpdateClassroomMaterialsRequest();
        first.setMaterials(java.util.List.of(
                assignment(10002L, ClassroomRole.TEACHER),
                assignment(10003L, ClassroomRole.STUDENT)
        ));
        classroomManagementAdapter.replaceMaterials(saved.getId(), first);

        UpdateClassroomMaterialsRequest second = new UpdateClassroomMaterialsRequest();
        second.setMaterials(java.util.List.of(
                assignment(10002L, ClassroomRole.STUDENT),
                assignment(10004L, ClassroomRole.TEACHER)
        ));

        assertDoesNotThrow(() -> classroomManagementAdapter.replaceMaterials(saved.getId(), second));

        Set<Long> ids = classroomManagementAdapter.getClassroomMaterials(saved.getId()).stream()
                .map(com.timcritt.tfg.domain.aggregate.classroom.MaterialReference::getMaterialId)
                .collect(Collectors.toSet());
        assertEquals(Set.of(10002L, 10004L), ids);
        assertEquals(2, ids.size());
    }

    @Test
    void replaceMaterials_succeedsWhenClassroomAlreadyHasTeacherMemberships() {
        Classroom saved = classroomManagementAdapter.save(new Classroom(null, "replace-materials-with-members", "desc"));

        TeacherDto teacher = new TeacherDto();
        teacher.setUserId(77L);
        classroomManagementAdapter.assignTeacherToClassroom(saved.getId(), teacher);

        UpdateClassroomMaterialsRequest request = new UpdateClassroomMaterialsRequest();
        request.setMaterials(java.util.List.of(
                assignment(10095L, ClassroomRole.TEACHER),
                assignment(10097L, ClassroomRole.TEACHER)
        ));

        assertDoesNotThrow(() -> classroomManagementAdapter.replaceMaterials(saved.getId(), request));

        Set<Long> ids = classroomManagementAdapter.getClassroomMaterials(saved.getId()).stream()
                .map(com.timcritt.tfg.domain.aggregate.classroom.MaterialReference::getMaterialId)
                .collect(Collectors.toSet());
        assertEquals(Set.of(10095L, 10097L), ids);
    }

    private static UpdateClassroomMaterialsRequest.MaterialAssignmentDto assignment(Long materialId, ClassroomRole role) {
        UpdateClassroomMaterialsRequest.MaterialAssignmentDto dto = new UpdateClassroomMaterialsRequest.MaterialAssignmentDto();
        dto.setMaterialId(materialId);
        dto.setAssignedToRole(role);
        return dto;
    }
}
