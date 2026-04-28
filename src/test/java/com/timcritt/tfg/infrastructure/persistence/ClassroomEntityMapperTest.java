package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.MaterialReference;
import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClassroomEntityMapperTest {

    private static final Long CLASSROOM_ID = 7L;
    private static final String CLASSROOM_NAME = "TOEFLMJ1930";
    private static final String CLASSROOM_DESCRIPTION = "TOEFL Tuesday and Thursday evenings";
    private static final String JOIN_CODE = "JOIN-123";
    private static final Long TEACHER_USER_ID = 43L;
    private static final String TEACHER_NAME = "Jane";
    private static final String TEACHER_SURNAME = "Doe";
    private static final Long MATERIAL_ID = 100L;
    private static final String MATERIAL_NAME = "Algebra sheet";
    private static final String MATERIAL_TYPE = "Worksheet";
    private static final Long STUDENT_USER_ID = 42L;
    private static final String STUDENT_NAME = "John";
    private static final String STUDENT_SURNAME = "Smith";

    @Test
    void preservesMembersAndMaterialsOnRoundTrip() {
        Classroom classroom = new Classroom(CLASSROOM_ID, CLASSROOM_NAME, CLASSROOM_DESCRIPTION, JOIN_CODE);
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());
        classroom.setMembers(List.of(
                new Member(null, TEACHER_USER_ID, TEACHER_NAME, TEACHER_SURNAME, ClassroomRole.TEACHER, Instant.now(), Instant.now())
        ));
        classroom.setMaterials(List.of(
                new MaterialReference(null, MATERIAL_ID, MATERIAL_NAME, MATERIAL_TYPE, ClassroomRole.TEACHER)
        ));

        var entity = ClassroomEntityMapper.toEntity(classroom);
        var roundTrip = ClassroomEntityMapper.toDomain(entity);

        assertNotNull(roundTrip);
        assertEquals(1, roundTrip.getMembers().size());
        assertEquals(1, roundTrip.getMaterials().size());
        assertEquals(100L, roundTrip.getMaterials().getFirst().getMaterialId());
        assertEquals("Algebra sheet", roundTrip.getMaterials().getFirst().getName());
        assertEquals(ClassroomRole.TEACHER, roundTrip.getMaterials().getFirst().getAssignedToRole());
    }

    @Test
    void deduplicatesRepeatedMembersDuringRoundTrip() {
        Instant now = Instant.now();
        ClassroomJpaEntity entity = new ClassroomJpaEntity("Math", "Math class", now, now);
        entity.setId(7L);

        MemberJpaEntity firstTeacher = new MemberJpaEntity();
        firstTeacher.setId(1L);
        firstTeacher.setUserId(TEACHER_USER_ID);
        firstTeacher.setName(TEACHER_NAME);
        firstTeacher.setSurname(TEACHER_SURNAME);
        firstTeacher.setRole(ClassroomRole.TEACHER);
        firstTeacher.setCreatedAt(now);
        firstTeacher.setUpdatedAt(now);

        MemberJpaEntity duplicateTeacher = new MemberJpaEntity();
        duplicateTeacher.setId(2L);
        duplicateTeacher.setUserId(TEACHER_USER_ID);
        duplicateTeacher.setName(TEACHER_NAME);
        duplicateTeacher.setSurname(TEACHER_SURNAME);
        duplicateTeacher.setRole(ClassroomRole.TEACHER);
        duplicateTeacher.setCreatedAt(now);
        duplicateTeacher.setUpdatedAt(now);

        MemberJpaEntity student = new MemberJpaEntity();
        student.setId(3L);
        student.setUserId(STUDENT_USER_ID);
        student.setName(STUDENT_NAME);
        student.setSurname(STUDENT_SURNAME);
        student.setRole(ClassroomRole.STUDENT);
        student.setCreatedAt(now);
        student.setUpdatedAt(now);

        entity.setMembers(List.of(firstTeacher, duplicateTeacher, student));
        entity.setMaterials(Set.of());

        Classroom domain = ClassroomEntityMapper.toDomain(entity);
        assertNotNull(domain);
        assertEquals(2, domain.getMembers().size());

        ClassroomJpaEntity roundTrip = ClassroomEntityMapper.toEntity(domain);
        assertEquals(2, roundTrip.getMembers().size());
    }
}

