package com.timcritt.tfg.application;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceAssignmentView;
import com.timcritt.tfg.application.port.outbound.repository.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaterialReferenceUpdateServiceTest {



    private static final class CapturingMaterialReferenceCommandPort implements MaterialReferenceRepositoryPort {
        private Long classroomId;
        private List<MaterialReference> desired;


        @Override
        public List<MaterialReference> findByClassroomId(Long classroomId) {
            return List.of();
        }

        @Override
        public List<MaterialReference> findByClassroomIdAndAssignedToRole(Long classroomId, ClassroomRole role) {
            return List.of();
        }

        @Override
        public List<MaterialReferenceAssignmentView> findAssignmentsByMaterialId(Long materialId) {
            return List.of();
        }

        @Override
        public List<MaterialReference> findByMaterialId(Long id) {
            return List.of();
        }


        @Override
        public int deleteByMaterialId(Long materialId) {
            return 0;
        }

        @Override
        public void save(MaterialReference materialReference) {

        }


    }
}


