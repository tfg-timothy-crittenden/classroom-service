package com.timcritt.tfg.infrastructure.web.dtoMapper;

import com.timcritt.tfg.application.query.ClassroomMemberView;
import com.timcritt.tfg.infrastructure.web.dto.ClassroomMemberDto;

public final class ClassroomMemberDtoMapper {

    private ClassroomMemberDtoMapper() {
    }

    public static ClassroomMemberDto toDto(
            ClassroomMemberView view
    ) {
        if (view == null) {
            return null;
        }

        return new ClassroomMemberDto(
                view.membershipId(),
                view.userId(),
                view.role(),
                view.firstName(),
                view.lastName()
        );
    }
}