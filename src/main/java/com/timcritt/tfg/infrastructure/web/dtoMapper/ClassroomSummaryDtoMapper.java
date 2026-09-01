package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.application.query.ClassroomSummaryView;
import com.timcritt.tfg.infrastructure.web.dto.ClassroomSummaryDto;
import com.timcritt.tfg.infrastructure.web.dto.TeacherSummaryDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClassroomSummaryDtoMapper {

    private final TeacherSummaryDtoMapper teacherSummaryDtoMapper = new TeacherSummaryDtoMapper();

    public ClassroomSummaryDto toDto(ClassroomSummaryView view) {
        if (view == null) {
            return null;
        }

        ClassroomSummaryDto dto = new ClassroomSummaryDto();
        dto.setId(view.id());
        dto.setName(view.name());
        dto.setDescription(view.description());
        dto.setCreatedAt(view.createdAt());
        dto.setUpdatedAt(view.updatedAt());
        dto.setStudentCount(view.studentCount());
        dto.setMaterialCount(view.materialCount());

        List<TeacherSummaryDto> teachers = view.teachers() == null
                ? List.of()
                : view.teachers().stream()
                .map(memberView -> teacherSummaryDtoMapper.toTeacherSummaryDto(memberView))
                .toList();
        dto.setTeachers(teachers);

        return dto;
    }
}
