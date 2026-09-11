package com.timcritt.tfg.application.port.inbound;

public interface MaterialDeletionProjectionUseCase {

    void handleMaterialDeleted(Long materialId);
}
