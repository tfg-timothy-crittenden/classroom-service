package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.cloud.config.enabled=false"
})
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    private ClassroomJpaRepository classroomJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void deletesOnlyTeacherMembershipsForTheUser() {
        ClassroomJpaEntity teacherClassroom = new ClassroomJpaEntity("Teacher classroom", "Teacher classroom", Instant.now(), Instant.now());
        MemberJpaEntity teacherMembership = new MemberJpaEntity();
        teacherMembership.setUserId(2L);
        teacherMembership.setRole(ClassroomRole.TEACHER);
        teacherMembership.setCreatedAt(Instant.now());
        teacherMembership.setUpdatedAt(Instant.now());
        teacherClassroom.addMember(teacherMembership);

        ClassroomJpaEntity studentClassroom = new ClassroomJpaEntity("Student classroom", "Student classroom", Instant.now(), Instant.now());
        MemberJpaEntity studentMembership = new MemberJpaEntity();
        studentMembership.setUserId(2L);
        studentMembership.setRole(ClassroomRole.STUDENT);
        studentMembership.setCreatedAt(Instant.now());
        studentMembership.setUpdatedAt(Instant.now());
        studentClassroom.addMember(studentMembership);

        classroomJpaRepository.save(teacherClassroom);
        classroomJpaRepository.save(studentClassroom);
        classroomJpaRepository.flush();

        int deleted = memberJpaRepository.deleteByUserIdAndRole(2L, ClassroomRole.TEACHER);

        assertEquals(1, deleted);
        assertEquals(1L, memberJpaRepository.count());
        assertTrue(classroomJpaRepository.findByIdWithMembersAndMaterials(teacherClassroom.getId())
                .orElseThrow()
                .getMembers()
                .isEmpty());
        assertEquals(1, classroomJpaRepository.findByIdWithMembersAndMaterials(studentClassroom.getId())
                .orElseThrow()
                .getMembers()
                .size());
    }
}

