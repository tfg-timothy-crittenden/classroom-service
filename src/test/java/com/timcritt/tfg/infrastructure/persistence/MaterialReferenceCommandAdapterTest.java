package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import com.timcritt.tfg.infrastructure.persistence.spring.MaterialReferenceJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MaterialReferenceCommandAdapterTest {

    @Test
    void updatesPartTitlesOnExistingMaterialReferenceDuringUpsert() {
        ClassroomJpaRepository classroomRepository = mock(ClassroomJpaRepository.class);
        MaterialReferenceJpaRepository materialReferenceRepository = mock(MaterialReferenceJpaRepository.class);
        MaterialReferenceCommandAdapter adapter = new MaterialReferenceCommandAdapter(classroomRepository, materialReferenceRepository);

        ClassroomJpaEntity classroom = new ClassroomJpaEntity("TOEFLMJ1930", "TOEFL class", Instant.now(), Instant.now());
        classroom.setId(1L);

        MaterialReferenceJpaEntity existing = new MaterialReferenceJpaEntity();
        existing.setId(10L);
        existing.setMaterialId(26L);
        existing.setName("Old title");
        existing.setDescription("Old description");
        existing.setPart1Title("Old part 1");
        existing.setPart2Title("Old part 2");
        existing.setAssignedToRole(ClassroomRole.TEACHER);
        classroom.addMaterial(existing);

        when(classroomRepository.findByIdWithMaterials(1L)).thenReturn(Optional.of(classroom));
        when(classroomRepository.save(any(ClassroomJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<com.timcritt.tfg.domain.model.MaterialReference> result = adapter.upsertForClassroom(
                1L,
                List.of(new com.timcritt.tfg.domain.model.MaterialReference(
                        null,
                        26L,
                        "New title",
                        "New description",
                        "New part 1",
                        "New part 2",
                        ClassroomRole.STUDENT
                ))
        );

        assertEquals(1, result.size());
        assertEquals("New title", result.getFirst().getName());
        assertEquals("New part 1", result.getFirst().getPart1Title());
        assertEquals("New part 2", result.getFirst().getPart2Title());

        ArgumentCaptor<ClassroomJpaEntity> classroomCaptor = ArgumentCaptor.forClass(ClassroomJpaEntity.class);
        verify(classroomRepository).save(classroomCaptor.capture());
        ClassroomJpaEntity saved = classroomCaptor.getValue();
        assertNotNull(saved);
        MaterialReferenceJpaEntity savedMaterial = saved.getMaterials().iterator().next();
        assertEquals("New title", savedMaterial.getName());
        assertEquals("New description", savedMaterial.getDescription());
        assertEquals("New part 1", savedMaterial.getPart1Title());
        assertEquals("New part 2", savedMaterial.getPart2Title());
        assertEquals(ClassroomRole.STUDENT, savedMaterial.getAssignedToRole());
        verify(classroomRepository).findByIdWithMaterials(eq(1L));
    }

    @Test
    void forwardsPartTitlesToRepositoryUpdateMethod() {
        ClassroomJpaRepository classroomRepository = mock(ClassroomJpaRepository.class);
        MaterialReferenceJpaRepository materialReferenceRepository = mock(MaterialReferenceJpaRepository.class);
        MaterialReferenceCommandAdapter adapter = new MaterialReferenceCommandAdapter(classroomRepository, materialReferenceRepository);

        MaterialReferenceJpaEntity reference = new MaterialReferenceJpaEntity();
        reference.setId(10L);
        reference.setMaterialId(26L);
        reference.setName("Old title");
        reference.setDescription("Old description");
        reference.setPart1Title("Old part 1");
        reference.setPart2Title("Old part 2");
        reference.setAssignedToRole(ClassroomRole.TEACHER);

        when(materialReferenceRepository.findByMaterialId(26L)).thenReturn(List.of(reference));

        int updated = adapter.updateTitlesByMaterialId(
                26L,
                "TOEFL Practice Test 2",
                "Part 1 - TOEFL Practice Test 2",
                "Part 2 - TOEFL Practice Test 2"
        );

        assertEquals(1, updated);
        assertEquals("TOEFL Practice Test 2", reference.getName());
        assertEquals("Part 1 - TOEFL Practice Test 2", reference.getPart1Title());
        assertEquals("Part 2 - TOEFL Practice Test 2", reference.getPart2Title());
        verify(materialReferenceRepository).findByMaterialId(26L);
        verify(materialReferenceRepository).saveAll(List.of(reference));
    }

    @Test
    void forwardsPartialTitlesWithoutMainTitleToRepositoryUpdateMethod() {
        ClassroomJpaRepository classroomRepository = mock(ClassroomJpaRepository.class);
        MaterialReferenceJpaRepository materialReferenceRepository = mock(MaterialReferenceJpaRepository.class);
        MaterialReferenceCommandAdapter adapter = new MaterialReferenceCommandAdapter(classroomRepository, materialReferenceRepository);

        MaterialReferenceJpaEntity reference = new MaterialReferenceJpaEntity();
        reference.setId(10L);
        reference.setMaterialId(26L);
        reference.setName("Old title");
        reference.setDescription("Old description");
        reference.setPart1Title("Old part 1");
        reference.setPart2Title("Old part 2");
        reference.setAssignedToRole(ClassroomRole.TEACHER);

        when(materialReferenceRepository.findByMaterialId(26L)).thenReturn(List.of(reference));

        int updated = adapter.updateTitlesByMaterialId(
                26L,
                null,
                "Part 1 - TOEFL Practice Test 2",
                null
        );

        assertEquals(1, updated);
        assertEquals("Old title", reference.getName());
        assertEquals("Part 1 - TOEFL Practice Test 2", reference.getPart1Title());
        assertEquals("Old part 2", reference.getPart2Title());
        verify(materialReferenceRepository).findByMaterialId(26L);
        verify(materialReferenceRepository).saveAll(List.of(reference));
    }
}

