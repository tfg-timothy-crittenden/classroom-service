package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.infrastructure.web.dto.MemberDto;

public final class MemberDtoMapper {
    private MemberDtoMapper() {
    }

    public static MemberDto toDto(Member member) {
        if (member == null) {
            return null;
        }

        MemberDto dto = new MemberDto();
        dto.setUserId(member.getUserId());
        dto.setRole(member.getRole());
        dto.setName(member.getName());
        dto.setSurname(member.getSurname());
        return dto;
    }
}

