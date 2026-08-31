package com.timcritt.tfg.application.port.outbound.repository;

import com.timcritt.tfg.domain.projection.MemberProfile;

public interface MemberProfileRepositoryPort {

    MemberProfile findByUserId(Long userId);
    void save(MemberProfile memberProfile);
    void deleteByUserId(Long userId);

}
