package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.inbound.MemberProfileProjectionUseCase;
import com.timcritt.tfg.application.port.outbound.repository.MemberProfileRepositoryPort;
import com.timcritt.tfg.application.service.useCase.MemberProfileProjectionUseCaseImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberProfileProjectionServiceAdapter {

    private final MemberProfileProjectionUseCase delegate;

    public MemberProfileProjectionServiceAdapter(
            MemberProfileRepositoryPort repository
    ) {
        this.delegate =
                new MemberProfileProjectionUseCaseImpl(repository);
    }

    @Transactional
    public void updateProfile(
            Long userId,
            Long version,
            String firstName,
            String lastName
    ) {
        delegate.updateProfile(
                userId,
                version,
                firstName,
                lastName
        );
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        delegate.deleteByUserId(userId);
    }
}