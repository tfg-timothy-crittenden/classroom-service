package com.timcritt.tfg.infrastructure.web;

import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.infrastructure.web.dto.TeacherSummaryDto;

public class TeacherSummaryDtoMapper {
    public TeacherSummaryDto toTeacherSummaryDto(Member member) {
        TeacherSummaryDto dto = new TeacherSummaryDto();
        dto.setMemberId(member.getId());
        dto.setUserId(member.getUserId());
        dto.setName(member.getName());
        dto.setSurname(member.getSurname());
        return dto;
    }
}

