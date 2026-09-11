package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.port.inbound.MaterialDeletionProjectionUseCase;
import com.timcritt.tfg.application.port.outbound.repository.MaterialDetailsRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MaterialReferenceRepositoryPort;

public class MaterialDeletionProjectionUseCaseImpl implements MaterialDeletionProjectionUseCase {

	private final MaterialReferenceRepositoryPort materialReferences;
	private final MaterialDetailsRepositoryPort materialDetails;

	public MaterialDeletionProjectionUseCaseImpl(
			MaterialReferenceRepositoryPort materialReferences,
			MaterialDetailsRepositoryPort materialDetails
	) {
		this.materialReferences = materialReferences;
		this.materialDetails = materialDetails;
	}

	@Override
	public void handleMaterialDeleted(Long materialId) {
		if (materialId == null || materialId <= 0) {
			throw new IllegalArgumentException("materialId must be positive");
		}
		materialReferences.deleteByMaterialId(materialId);
		materialDetails.deleteByMaterialId(materialId);
	}
}
