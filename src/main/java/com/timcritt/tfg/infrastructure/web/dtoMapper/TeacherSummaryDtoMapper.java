package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.infrastructure.web.dto.TeacherSummaryDto;

public class TeacherSummaryDtoMapper {
    public TeacherSummaryDto toTeacherSummaryDto(Membership membership) {
        TeacherSummaryDto dto = new TeacherSummaryDto();
        dto.setMemberId(membership.getId());
        dto.setUserId(membership.getUserId());
        dto.setName(membership.getName());
        dto.setSurname(membership.getSurname());
        return dto;
    }
}

