package com.timcritt.tfg.application.port.outbound;
import com.timcritt.tfg.domain.model.MaterialReference;
import java.util.List;
public interface MaterialReferenceCommandPort {
    List<MaterialReference> upsertForClassroom(Long classroomId, List<MaterialReference> desired);

    int deleteByMaterialId(Long materialId);

    int updateTitlesByMaterialId(Long materialId, String title, String part1Title, String part2Title);
}
