package com.timcritt.tfg.domain.aggregate.classroom;

import com.timcritt.tfg.domain.exception.InvalidClassroomMaterialsException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClassroomTest {

    @Test
    void replaceMaterialsAcceptsEmptyList() {
        Classroom classroom = new Classroom(1L, "Math", "Desc");

        classroom.replaceMaterials(List.of());

        assertEquals(0, classroom.getMaterials().size());
    }

    @Test
    void replaceMaterialsRejectsDuplicateMaterialIds() {
        Classroom classroom = new Classroom(1L, "Math", "Desc");

        assertThrows(InvalidClassroomMaterialsException.class, () -> classroom.replaceMaterials(List.of(
                new MaterialReference(null, 10002L, ClassroomRole.TEACHER),
                new MaterialReference(null, 10002L, ClassroomRole.STUDENT)
        )));
    }

    @Test
    void replaceMaterialsReplacesCurrentList() {
        Classroom classroom = new Classroom(1L, "Math", "Desc");
        classroom.replaceMaterials(List.of(new MaterialReference(null, 1L, ClassroomRole.TEACHER)));

        classroom.replaceMaterials(List.of(new MaterialReference(null, 2L, ClassroomRole.STUDENT)));

        assertEquals(1, classroom.getMaterials().size());
        assertEquals(2L, classroom.getMaterials().getFirst().getMaterialId());
    }
}
