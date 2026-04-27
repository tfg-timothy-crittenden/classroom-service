package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.application.service.MaterialAccessAuthorizationService;
import com.timcritt.tfg.application.service.MaterialAccessDecision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialAccessAuthorizationServiceAdapter {

    private final MaterialAccessAuthorizationService delegate;

    public MaterialAccessAuthorizationServiceAdapter(
            MaterialReferenceRepositoryPort materialReferenceRepository,
            MemberRepositoryPort memberRepository
    ) {
        this.delegate = new MaterialAccessAuthorizationService(materialReferenceRepository, memberRepository);
    }

    @Transactional(readOnly = true)
    public MaterialAccessDecision checkMaterialAccess(String userId, Long materialId, String action) {
        return delegate.checkReadAccess(userId, materialId, action);
    }
}

