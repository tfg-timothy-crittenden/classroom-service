package com.timcritt.tfg.infrastructure.web;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.service.ClassroomServiceAdapter;
import com.timcritt.tfg.infrastructure.service.MaterialReferenceServiceAdapter;
import com.timcritt.tfg.infrastructure.service.MemberRoleServiceAdapter;
import com.timcritt.tfg.infrastructure.service.MaterialReferenceUpdateServiceAdapter;
import com.timcritt.tfg.infrastructure.web.dto.ClassroomDto;
import com.timcritt.tfg.infrastructure.web.dto.ClassroomSummaryDto;
import com.timcritt.tfg.infrastructure.web.dto.MaterialReferenceDto;
import com.timcritt.tfg.infrastructure.web.dto.RoleCheckDto;
import com.timcritt.tfg.infrastructure.web.dto.UpdateClassroomMaterialsRequest;
import com.timcritt.tfg.infrastructure.web.dtoMapper.ClassroomDtoMapper;
import com.timcritt.tfg.infrastructure.web.dtoMapper.MaterialReferenceDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
@Slf4j
public class ClassroomController {

    private final ClassroomServiceAdapter getClassroomQuery;
    private final ClassroomDtoMapper classroomDtoMapper;
    private final MaterialReferenceServiceAdapter materialReferenceQuery;
    private final MemberRoleServiceAdapter memberRoleService;
    private final MaterialReferenceUpdateServiceAdapter materialReferenceUpdate;

    @GetMapping("/member/{userId}")
    public List<ClassroomDto> getClassroomsByMember(@PathVariable Long userId) {
        return getClassroomQuery.getClassroomsByMember(userId).stream()
                .map(classroomDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/summary/member/{userId}")
    public List<ClassroomSummaryDto> getClassroomSummariesByMember(@PathVariable Long userId) {
        return getClassroomQuery.getClassroomsByMember(userId).stream()
                .map(classroomDtoMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{classroomId}/materials")
    public List<MaterialReferenceDto> getMaterialsByClassroom(@PathVariable Long classroomId) {
        return materialReferenceQuery.getMaterialsByClassroom(classroomId).stream()
                .map(MaterialReferenceDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{classroomId}/materials/role/{role}")
    public List<MaterialReferenceDto> getMaterialsByClassroomAndRole(
            @PathVariable Long classroomId,
            @PathVariable ClassroomRole role
    ) {
        return materialReferenceQuery.getMaterialsByClassroomAndRole(classroomId, role).stream()
                .map(MaterialReferenceDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{classroomId}/members/{userId}/role")
    public RoleCheckDto getRoleInClassroom(
            @PathVariable Long classroomId,
            @PathVariable Long userId
    ) {
        RoleCheckDto dto = new RoleCheckDto();
        dto.setClassroomId(classroomId);
        dto.setUserId(userId);
        dto.setRole(memberRoleService.getRoleInClassroom(classroomId, userId).orElse(null));
        return dto;
    }

    @GetMapping
    public List<ClassroomSummaryDto> getAllClassroomSummaries() {
        return getClassroomQuery.getAllClassrooms().stream()
                .map(classroomDtoMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{classroomId}/materials")
    public List<MaterialReferenceDto> updateClassroomMaterials(
            @PathVariable Long classroomId,
            @RequestBody UpdateClassroomMaterialsRequest request
    ) {
        int materialCount = (request == null || request.getMaterials() == null) ? 0 : request.getMaterials().size();
        log.info("PUT /api/classrooms/{}/materials called. materialsCount={}", classroomId, materialCount);
        log.debug("PUT /api/classrooms/{}/materials requestBody={}", classroomId, request);

        List<UpdateClassroomMaterialsCommand.MaterialAssignment> desired = request == null || request.getMaterials() == null
                ? List.of()
                : request.getMaterials().stream()
                .map(m -> new UpdateClassroomMaterialsCommand.MaterialAssignment(
                        m.getMaterialId(),
                        m.getName(),
                        m.getDescription(),
                        m.getAssignedToRole()
                ))
                .toList();

        UpdateClassroomMaterialsCommand command = new UpdateClassroomMaterialsCommand(classroomId, desired);

        return materialReferenceUpdate.updateClassroomMaterials(command).stream()
                .map(MaterialReferenceDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
