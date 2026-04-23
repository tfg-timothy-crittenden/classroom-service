package com.timcritt.tfg.infrastructure.web;

import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.web.dto.ClassroomDto;
import com.timcritt.tfg.infrastructure.web.dto.ClassroomSummaryDto;
import com.timcritt.tfg.infrastructure.web.dto.TeacherSummaryDto;
import com.timcritt.tfg.infrastructure.web.dto.MemberDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClassroomDtoMapper {
    private final TeacherSummaryDtoMapper teacherSummaryDtoMapper = new TeacherSummaryDtoMapper();

    public ClassroomDto toDto(Classroom classroom) {
        if (classroom == null) return null;
        ClassroomDto dto = new ClassroomDto();
        dto.setId(classroom.getId());
        dto.setName(classroom.getName());
        dto.setDescription(classroom.getDescription());
        dto.setCreatedAt(classroom.getCreatedAt());
        dto.setUpdatedAt(classroom.getUpdatedAt());
        // Only map teachers and student count
        if (classroom.getMembers() != null) {
            dto.setMembers(classroom.getMembers().stream()
                .filter(member -> member.getRole() == ClassroomRole.TEACHER)
                .map(member -> {
                    com.timcritt.tfg.infrastructure.web.dto.MemberDto memberDto = new MemberDto();
                    memberDto.setUserId(member.getUserId());
                    memberDto.setRole(member.getRole());
                    memberDto.setName(member.getName());
                    memberDto.setSurname(member.getSurname());
                    return memberDto;
                })
                .collect(java.util.stream.Collectors.toList()));
            dto.setStudentCount((int) classroom.getMembers().stream()
                .filter(member -> member.getRole() == ClassroomRole.STUDENT)
                .count());
        } else {
            dto.setStudentCount(0);
        }
        return dto;
    }

    public ClassroomSummaryDto toSummaryDto(Classroom classroom) {
        if (classroom == null) return null;
        ClassroomSummaryDto dto = new ClassroomSummaryDto();
        dto.setId(classroom.getId());
        dto.setName(classroom.getName());
        dto.setDescription(classroom.getDescription());
        dto.setCreatedAt(classroom.getCreatedAt());
        dto.setUpdatedAt(classroom.getUpdatedAt());

        dto.setMaterialCount(classroom.getMaterials() == null ? 0 : classroom.getMaterials().size());

        if (classroom.getMembers() != null) {
            // Count students
            dto.setStudentCount((int) classroom.getMembers().stream()
                .filter(member -> member.getRole() == ClassroomRole.STUDENT)
                .count());
            // Map teachers
            List<TeacherSummaryDto> teachers = classroom.getMembers().stream()
                .filter(member -> member.getRole() == ClassroomRole.TEACHER)
                .map(teacherSummaryDtoMapper::toTeacherSummaryDto)
                .collect(Collectors.toList());
            dto.setTeachers(teachers);
        } else {
            dto.setStudentCount(0);
        }
        return dto;
    }
}
