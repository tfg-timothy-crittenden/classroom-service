package com.timcritt.tfg.infrastructure.web.controller;

import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.service.ClassroomServiceAdapter;
import com.timcritt.tfg.infrastructure.service.ClassroomAuthorizationService;
import com.timcritt.tfg.infrastructure.service.MaterialReferenceServiceAdapter;
import com.timcritt.tfg.infrastructure.service.MaterialReferenceUpdateServiceAdapter;
import com.timcritt.tfg.infrastructure.web.dto.*;
import com.timcritt.tfg.infrastructure.web.dtoMapper.ClassroomDtoMapper;
import com.timcritt.tfg.infrastructure.web.dtoMapper.MaterialReferenceDtoMapper;
import com.timcritt.tfg.infrastructure.web.dtoMapper.MemberDtoMapper;
import com.timcritt.tfg.infrastructure.web.dtoMapper.RoleCheckDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
@Slf4j
public class ClassroomController {


    private final ClassroomServiceAdapter classroomService;
    private final ClassroomAuthorizationService classroomAuthorizationService;
    private final ClassroomDtoMapper classroomDtoMapper;
    private final MaterialReferenceServiceAdapter materialReferenceQuery;
    private final MaterialReferenceUpdateServiceAdapter materialReferenceUpdate;

    //Only accessible to members of the classroom and to admin
    @GetMapping("/{classroomId}/members/teachers")
    public List<MemberDto> getTeachersByClassroom(Authentication authentication, @PathVariable Long classroomId) {
        classroomAuthorizationService.ensureCanReadTeachers(authentication, classroomId);
        return classroomService.getTeachersByClassroomId(classroomId).stream()
                .map(MemberDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    //Only accessible to teachers assigned to this classroom and to admin
    @GetMapping("/{classroomId}/members/students")
    public List<MemberDto> getStudentsByClassroom(Authentication authentication, @PathVariable Long classroomId) {
        classroomAuthorizationService.ensureCanReadStudents(authentication, classroomId);
        return classroomService.getStudentsByClassroomId(classroomId).stream()
                .map(MemberDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    //Only accessible to the authenticated user with that userId and to admin
    @GetMapping("/member/{userId}")
    public List<ClassroomDto> getClassroomsByMember(Authentication authentication, @PathVariable Long userId) {
        classroomAuthorizationService.ensureCanReadMemberClassrooms(authentication, userId);
        return classroomService.getClassroomsByMember(userId).stream()
                .map(classroomDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    //Only accessible to the authenticated user with that userId and to admin
    @GetMapping("/summary/member/{userId}")
    public List<ClassroomSummaryDto> getClassroomSummariesByMember(Authentication authentication, @PathVariable Long userId) {
        classroomAuthorizationService.ensureCanReadMemberSummaries(authentication, userId);
        return classroomService.getClassroomsByMember(userId).stream()
                .map(classroomDtoMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    //Only accessible to the authenticated user, the classroom teacher, or admin
    @DeleteMapping("/{classroomId}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromClassroom(
            Authentication authentication,
            @PathVariable Long classroomId,
            @PathVariable Long userId
    ) {
        classroomAuthorizationService.ensureCanRemoveMember(authentication, classroomId, userId);
        boolean removed = classroomService.removeMemberFromClassroom(classroomId, userId);
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    //Only for ad //TODO check that this is still needed
    @GetMapping("/{classroomId}/materials")
    public List<MaterialReferenceDto> getMaterialsByClassroom(Authentication authentication, @PathVariable Long classroomId) {
        classroomAuthorizationService.ensureSystemAdmin(authentication);
        return materialReferenceQuery.getMaterialsByClassroom(classroomId).stream()
                .map(MaterialReferenceDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    //Only be accessible to members of that classroom and system admin. Teachers of the classroom can see all. Student members of classroom can only access material assigned to student members.
    @GetMapping("/{classroomId}/materials/role/{role}")
    public List<MaterialReferenceDto> getMaterialsByClassroomAndRole(
            Authentication authentication,
            @PathVariable Long classroomId,
            @PathVariable ClassroomRole role
    ) {
        classroomAuthorizationService.ensureCanReadMaterials(authentication, classroomId, role);
        return materialReferenceQuery.getMaterialsByClassroomAndRole(classroomId, role).stream()
                .map(MaterialReferenceDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    //Only accessible to the authenticated user with that userId
    @GetMapping("/{classroomId}/members/{userId}/role")
    public RoleCheckDto getRoleInClassroom(Authentication authentication,
            @PathVariable Long classroomId,
            @PathVariable Long userId
    ) {
        classroomAuthorizationService.ensureCanReadMemberRole(authentication, userId);
        return RoleCheckDtoMapper.toDto(
                classroomId,
                userId,
                classroomService.getRoleInClassroom(classroomId, userId).orElse(null)
        );
    }

    //Only be accessible by an authenticated system admin
    @PostMapping("/{classroomId}/assign-teacher")
    public ResponseEntity<ClassroomDto> assignTeacher(
            Authentication authentication,
            @PathVariable Long classroomId,
            @RequestBody TeacherDto teacherDto
    ) {
        classroomAuthorizationService.ensureSystemAdmin(authentication);
        Classroom classroom = classroomService.assignTeacherToClassroom(classroomId, teacherDto);
        return ResponseEntity.ok(classroomDtoMapper.toDto(classroom));
    }

    //Only for authenticated admins
    @GetMapping
    public List<ClassroomSummaryDto> getAllClassroomSummaries(Authentication authentication) {
        classroomAuthorizationService.ensureSystemAdmin(authentication);
        return classroomService.getAllClassrooms().stream()
                .map(classroomDtoMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    //Only for authenticated admins
    @PutMapping("/{classroomId}/materials")
    public List<MaterialReferenceDto> updateClassroomMaterials(
            Authentication authentication,
            @PathVariable Long classroomId,
            @RequestBody UpdateClassroomMaterialsRequest request
    ) {
        classroomAuthorizationService.ensureSystemAdmin(authentication);
        return materialReferenceUpdate.updateClassroomMaterials(classroomId, request).stream()
                .map(MaterialReferenceDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    //Only accessible to authenticated admins
    @PostMapping()
    public ResponseEntity<ClassroomDto> createClassroom(
            Authentication authentication,
            @RequestBody CreateClassroomRequest request
    ) {
        classroomAuthorizationService.ensureSystemAdmin(authentication);
        // Map the request to a domain Classroom
        Classroom classroom = new Classroom(null, request.getName(), request.getDescription());
        // Save the classroom using the service
        Classroom saved = classroomService.save(classroom);
        // Return the DTO wrapped in a ResponseEntity
        return ResponseEntity.ok(classroomDtoMapper.toDto(saved));
    }

    //Only accessible to authenticated admins
    @PutMapping("/{classroomId}/teachers")
    public ResponseEntity<ClassroomDto> syncTeachers(
            Authentication authentication,
            @PathVariable Long classroomId,
            @RequestBody SyncTeachersRequest request
    ) {
        classroomAuthorizationService.ensureSystemAdmin(authentication);
        Classroom classroom = classroomService.syncTeachersForClassroom(classroomId, request.getTeachers());
        return ResponseEntity.ok(classroomDtoMapper.toDto(classroom));
    }

    //Only accessible to authenticated admins
    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(Authentication authentication, @PathVariable Long classroomId) {
        classroomAuthorizationService.ensureSystemAdmin(authentication);
        classroomService.deleteClassroomById(classroomId);
        return ResponseEntity.noContent().build();
    }

    //Only accessible to authenticated admins
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteClassroomsBatch(Authentication authentication, @RequestBody DeleteClassroomsRequest request) {
        classroomAuthorizationService.ensureSystemAdmin(authentication);
        classroomService.deleteClassroomsByIds(request.getClassroomIds());
        return ResponseEntity.noContent().build();
    }

    //Only accessible to teachers of that classroom and admins
    @GetMapping("/{classroomId}/join-code")
    public ResponseEntity<String> getJoinCode(Authentication authentication, @PathVariable Long classroomId) {
        classroomAuthorizationService.ensureCanReadJoinCode(authentication, classroomId);
        Classroom classroom = classroomService.getClassroomById(classroomId);
        if (classroom == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(classroom.getJoinCode());
    }

    //Accessible to any authenticated user
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
