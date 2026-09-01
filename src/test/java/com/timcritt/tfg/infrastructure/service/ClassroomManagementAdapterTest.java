package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import com.timcritt.tfg.application.port.outbound.MaterialDetailsRequestPublisherPort;
import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MaterialDetailsRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MembershipRepositoryPort;
import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.domain.projection.MaterialDetails;
import com.timcritt.tfg.domain.aggregate.classroom.MaterialReference;
import com.timcritt.tfg.infrastructure.web.dto.MaterialReferenceWithDetailsDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClassroomManagementAdapterTest {

    @Test
    void enrichesMaterialsByRoleWithMaterialDetails() {
        ClassroomRepositoryPort classroomRepository = mock(ClassroomRepositoryPort.class);
        MembershipRepositoryPort memberRepository = mock(MembershipRepositoryPort.class);
        JoinCodeGenerator joinCodeGenerator = mock(JoinCodeGenerator.class);
        MemberRoleServiceAdapter memberRoleService = mock(MemberRoleServiceAdapter.class);
        MaterialDetailsRepositoryPort materialDetailsRepository = mock(MaterialDetailsRepositoryPort.class);
        MaterialDetailsRequestPublisherPort materialDetailsRequestPublisher = mock(MaterialDetailsRequestPublisherPort.class);

        ClassroomManagementAdapter adapter = new ClassroomManagementAdapter(
                classroomRepository,
                memberRepository,
                joinCodeGenerator,
                memberRoleService,
                materialDetailsRepository,
                materialDetailsRequestPublisher
        );

        Classroom classroom = new Classroom(7L, "classroom", "desc");
        classroom.replaceMaterials(List.of(new MaterialReference(1L, 101L, ClassroomRole.STUDENT)));
        when(classroomRepository.findById(7L)).thenReturn(classroom);
        when(materialDetailsRepository.findByMaterialId(101L))
                .thenReturn(MaterialDetails.builder()
                        .materialId(101L)
                        .name("Speaking Part 1")
                        .description("Practice response")
                        .part1Title("Warm-up")
                        .part2Title("Main task")
                        .build());

        List<MaterialReferenceWithDetailsDto> result = adapter.getClassroomMaterialsByRole(7L, ClassroomRole.STUDENT);

        assertEquals(1, result.size());
        MaterialReferenceWithDetailsDto dto = result.getFirst();
        assertEquals(101L, dto.getMaterialId());
        assertEquals(ClassroomRole.STUDENT, dto.getAssignedToRole());
        assertEquals("Speaking Part 1", dto.getName());
        assertEquals("Practice response", dto.getDescription());
        assertEquals("Warm-up", dto.getPart1Title());
        assertEquals("Main task", dto.getPart2Title());

        verify(classroomRepository).findById(7L);
        verify(materialDetailsRepository).findByMaterialId(101L);
    }
}

