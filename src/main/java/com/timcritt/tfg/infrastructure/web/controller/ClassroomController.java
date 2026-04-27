package com.timcritt.tfg.infrastructure.web.controller;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.service.ClassroomServiceAdapter;
import com.timcritt.tfg.infrastructure.service.MaterialReferenceServiceAdapter;
import com.timcritt.tfg.infrastructure.service.MemberRoleServiceAdapter;
import com.timcritt.tfg.infrastructure.service.MaterialReferenceUpdateServiceAdapter;
import com.timcritt.tfg.infrastructure.web.dto.*;
import com.timcritt.tfg.infrastructure.web.dtoMapper.ClassroomDtoMapper;
import com.timcritt.tfg.infrastructure.web.dtoMapper.MaterialReferenceDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
@Slf4j
public class ClassroomController {


    private final ClassroomServiceAdapter classroomService;
    private final ClassroomDtoMapper classroomDtoMapper;
    private final MaterialReferenceServiceAdapter materialReferenceQuery;
    private final MemberRoleServiceAdapter memberRoleService;
    private final MaterialReferenceUpdateServiceAdapter materialReferenceUpdate;


    @GetMapping("/{classroomId}/members/teachers")
    public List<MemberDto> getTeachersByClassroom(@PathVariable Long classroomId) {
        return classroomService.getTeachersByClassroomId(classroomId);
    }

    @GetMapping("/{classroomId}/members/students")
    public List<MemberDto> getStudentsByClassroom(@PathVariable Long classroomId) {
        return classroomService.getStudentsByClassroomId(classroomId);
    }

    @GetMapping("/member/{userId}")
    public List<ClassroomDto> getClassroomsByMember(@PathVariable Long userId) {
        return classroomService.getClassroomsByMember(userId).stream()
                .map(classroomDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/summary/member/{userId}")
    public List<ClassroomSummaryDto> getClassroomSummariesByMember(@PathVariable Long userId) {
        return classroomService.getClassroomsByMember(userId).stream()
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
    @PostMapping("/{classroomId}/assign-teacher")
    public ResponseEntity<ClassroomDto> assignTeacher(
            @PathVariable Long classroomId,
            @RequestBody TeacherDto teacherDto
    ) {
        Classroom classroom = classroomService.assignTeacherToClassroom(classroomId, teacherDto);
        return ResponseEntity.ok(classroomDtoMapper.toDto(classroom));
    }

    @GetMapping
    public List<ClassroomSummaryDto> getAllClassroomSummaries() {
        return classroomService.getAllClassrooms().stream()
                .map(classroomDtoMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{classroomId}/materials")
    public List<MaterialReferenceDto> updateClassroomMaterials(
            @PathVariable Long classroomId,
            @RequestBody UpdateClassroomMaterialsRequest request
    ) {
        log.info("PUT /api/classrooms/{}/materials called. materialsCount={}", classroomId, (request == null || request.getMaterials() == null) ? 0 : request.getMaterials().size());
        log.debug("PUT /api/classrooms/{}/materials requestBody={}", classroomId, request);
        return materialReferenceUpdate.updateClassroomMaterials(classroomId, request).stream()
                .map(MaterialReferenceDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping()
    public ResponseEntity<ClassroomDto> createClassroom(
            @RequestBody CreateClassroomRequest request
    ) {
        // Map the request to a domain Classroom
        Classroom classroom = new Classroom(null, request.getName(), request.getDescription());
        // Save the classroom using the service
        Classroom saved = classroomService.save(classroom);
        // Return the DTO wrapped in a ResponseEntity
        return ResponseEntity.ok(classroomDtoMapper.toDto(saved));
    }

    @PutMapping("/{classroomId}/teachers")
    public ResponseEntity<ClassroomDto> syncTeachers(
            @PathVariable Long classroomId,
            @RequestBody SyncTeachersRequest request
    ) {
        Classroom classroom = classroomService.syncTeachersForClassroom(classroomId, request.getTeachers());
        return ResponseEntity.ok(classroomDtoMapper.toDto(classroom));
    }

    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long classroomId) {
        classroomService.deleteClassroomById(classroomId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteClassroomsBatch(@RequestBody DeleteClassroomsRequest request) {
        classroomService.deleteClassroomsByIds(request.getClassroomIds());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{classroomId}/join-code")
    public ResponseEntity<String> getJoinCode(@PathVariable Long classroomId) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(classroom.getJoinCode());
    }

    @PostMapping("/join")
    public ResponseEntity<ClassroomDto> joinClassroom(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody JoinClassroomRequest request) {

        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userIdStr = jwt.getClaimAsString("userId");
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userIdStr);
        String name = jwt.getClaimAsString("name");
        String surname = jwt.getClaimAsString("surname");

        Classroom classroom = classroomService.joinClassroom(
                userId,
                request.getJoinCode(),
                name,
                surname
        );

        return ResponseEntity.ok(classroomDtoMapper.toDto(classroom));
    }
}
