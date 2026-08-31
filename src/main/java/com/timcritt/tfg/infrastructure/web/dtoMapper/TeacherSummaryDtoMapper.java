package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.infrastructure.web.dto.TeacherSummaryDto;

public class TeacherSummaryDtoMapper {
    public TeacherSummaryDto toTeacherSummaryDto(Membership membership) {
        TeacherSummaryDto dto = new TeacherSummaryDto();
        dto.setMemberId(membership.getId());
        dto.setUserId(membership.getUserId());
        return dto;
    }
}

