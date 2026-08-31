package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.port.inbound.MemberProfileProjectionUseCase;
import com.timcritt.tfg.application.port.outbound.repository.MemberProfileRepositoryPort;
import com.timcritt.tfg.domain.projection.MemberProfile;

public class MemberProfileProjectionUseCaseImpl implements MemberProfileProjectionUseCase {

    private final MemberProfileRepositoryPort memberProfileRepository;

    public MemberProfileProjectionUseCaseImpl(MemberProfileRepositoryPort repository) {
        this.memberProfileRepository = repository;
    }

    @Override
    public void deleteByUserId(Long userId) {
        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        memberProfileRepository.deleteByUserId(userId);
    }

    @Override
    public void updateProfile(Long userId, Long version, String firstName, String lastName) {

        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if(version == null) {
            throw new IllegalArgumentException("version is required");
        }
        if (version < 0) {
            throw new IllegalArgumentException("version cannot be negative");
        }

        MemberProfile memberProfile = memberProfileRepository.findByUserId(userId);

        if(memberProfile == null) {
            MemberProfile newMemberProfile = MemberProfile.builder()
                    .userId(userId)
                    .version(version)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
            memberProfileRepository.save(newMemberProfile);
            return;
        }

        long currentVersion = memberProfile.getVersion() == null ? 0 : memberProfile.getVersion();
        if(version <= currentVersion) {
            return;
        }
        memberProfile.updateProfile(firstName, lastName);
        memberProfile.updateVersion(version);
        memberProfileRepository.save(memberProfile);
    }
}
