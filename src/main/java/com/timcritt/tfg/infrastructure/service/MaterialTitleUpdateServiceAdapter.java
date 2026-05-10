package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.application.service.MaterialTitleUpdateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialTitleUpdateServiceAdapter {

    private final MaterialTitleUpdateService delegate;

    public MaterialTitleUpdateServiceAdapter(MaterialReferenceCommandPort commandPort) {
        this.delegate = new MaterialTitleUpdateService(commandPort);
    }

    @Transactional
    public int updateTitle(Long materialId, String title, String part1Title, String part2Title) {
        return delegate.updateTitle(materialId, title, part1Title, part2Title);
    }
}
