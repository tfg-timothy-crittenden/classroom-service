package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;
import com.timcritt.tfg.application.service.MaterialReferenceUpdateService;
import com.timcritt.tfg.domain.model.MaterialReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaterialReferenceUpdateServiceAdapter {

    private final MaterialReferenceUpdateService delegate;

    public MaterialReferenceUpdateServiceAdapter(MaterialReferenceCommandPort commandPort) {
        this.delegate = new MaterialReferenceUpdateService(commandPort);
    }

    @Transactional
    public List<MaterialReference> updateClassroomMaterials(UpdateClassroomMaterialsCommand command) {
        return delegate.updateClassroomMaterials(command);
    }
}
