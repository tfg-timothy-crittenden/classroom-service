package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.repository.MemberProfileRepositoryPort;
import com.timcritt.tfg.infrastructure.persistence.MemberProfileEntityMapper;
import com.timcritt.tfg.infrastructure.persistence.spring.MemberProfileJpaRepository;
import com.timcritt.tfg.domain.projection.MemberProfile;
import org.springframework.stereotype.Repository;

@Repository
public class MemberProfileRepositoryAdapter
        implements MemberProfileRepositoryPort {

    private final MemberProfileJpaRepository repository;

    public MemberProfileRepositoryAdapter(
            MemberProfileJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public MemberProfile findByUserId(Long userId) {
        return repository.findById(userId)
                .map(MemberProfileEntityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public void save(MemberProfile memberProfile) {
        repository.save(
                MemberProfileEntityMapper.toEntity(memberProfile)
        );
    }

    @Override
    public void deleteByUserId(Long userId) {
        repository.deleteById(userId);
    }
}