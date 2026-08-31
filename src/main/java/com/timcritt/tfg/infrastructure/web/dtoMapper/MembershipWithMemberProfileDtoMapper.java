package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.domain.projection.MemberProfile;
import com.timcritt.tfg.infrastructure.web.dto.MembershipWithMemberProfile;

public final class MembershipWithMemberProfileDtoMapper {

    private MembershipWithMemberProfileDtoMapper() {
    }

    public static MembershipWithMemberProfile toDto(
            Membership membership,
            MemberProfile memberProfile
    ) {
        if (membership == null) {
            return null;
        }

        MembershipWithMemberProfile dto = new MembershipWithMemberProfile();

        dto.setUserId(membership.getUserId());
        dto.setRole(membership.getRole());

        if (memberProfile != null) {
            dto.setFirstName(memberProfile.getFirstName());
            dto.setLastName(memberProfile.getLastName());
        }

        return dto;
    }
}