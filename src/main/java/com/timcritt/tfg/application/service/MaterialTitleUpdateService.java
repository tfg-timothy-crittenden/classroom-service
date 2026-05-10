package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceCommandPort;

/**
 * Application service (no Spring) that updates classroom material reference titles from upstream material events.
 */
public class MaterialTitleUpdateService {

    private final MaterialReferenceCommandPort commandPort;

    public MaterialTitleUpdateService(MaterialReferenceCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    public int updateTitle(Long materialId, String title, String part1Title, String part2Title) {
        if (materialId == null) {
            throw new IllegalArgumentException("materialId is required");
        }
        String normalizedTitle = normalize(title);
        String normalizedPart1Title = normalize(part1Title);
        String normalizedPart2Title = normalize(part2Title);

        if (normalizedTitle == null && normalizedPart1Title == null && normalizedPart2Title == null) {
            throw new IllegalArgumentException("at least one title field is required");
        }
        return commandPort.updateTitlesByMaterialId(materialId, normalizedTitle, normalizedPart1Title, normalizedPart2Title);
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
