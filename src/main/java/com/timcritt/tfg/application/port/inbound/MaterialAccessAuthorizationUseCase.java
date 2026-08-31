package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.domain.model.ClassroomRole;

import java.util.Optional;

public interface MaterialAccessAuthorizationUseCase {

    Optional<ClassroomRole> getMemberClassroomRole(Long classroomId, Long userId);

}
