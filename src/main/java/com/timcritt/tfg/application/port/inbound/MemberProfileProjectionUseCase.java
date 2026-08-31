package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.domain.projection.MemberProfile;

public interface MemberProfileProjectionUseCase {

    void deleteByUserId(Long userId);
    void updateProfile(Long UserId, Long version, String firstName, String lastName);

}
