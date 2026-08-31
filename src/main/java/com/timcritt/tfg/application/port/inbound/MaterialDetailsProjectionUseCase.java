package com.timcritt.tfg.application.port.inbound;

public interface MaterialDetailsProjectionUseCase {

    void deleteByMaterialId(Long materialId);
    void updateDetails(Long materialId, Long version, String title, String part1Title, String part2Title, String description);

}
