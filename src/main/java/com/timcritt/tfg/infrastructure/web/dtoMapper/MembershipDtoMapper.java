package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.infrastructure.web.dto.MembershipDto;

public final class MembershipDtoMapper {
    private MembershipDtoMapper() {
    }

    public static MembershipDto toDto(Membership membership) {
        if (membership == null) {
            return null;
        }

        MembershipDto dto = new MembershipDto();
        dto.setUserId(membership.getUserId());
        dto.setRole(membership.getRole());
        return dto;
    }
}

