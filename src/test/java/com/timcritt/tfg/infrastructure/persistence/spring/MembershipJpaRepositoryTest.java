package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MembershipJpaEntity;
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
        "spring.cloud.config.enabled=false",
        "classroom.grpc.enabled=false"
})
@Transactional
class MembershipJpaRepositoryTest {

    @Autowired
    private ClassroomJpaRepository classroomJpaRepository;

    @Autowired
    private MembershipJpaRepository membershipJpaRepository;

    @Test
    void deletesOnlyTeacherMembershipsForTheUser() {
        ClassroomJpaEntity teacherClassroom = new ClassroomJpaEntity("Teacher classroom", "Teacher classroom", Instant.now(), Instant.now());
        MembershipJpaEntity teacherMembership = new MembershipJpaEntity();
        teacherMembership.setUserId(2L);
        teacherMembership.setRole(ClassroomRole.TEACHER);
        teacherMembership.setCreatedAt(Instant.now());
        teacherMembership.setUpdatedAt(Instant.now());
        teacherClassroom.addMember(teacherMembership);

        ClassroomJpaEntity studentClassroom = new ClassroomJpaEntity("Student classroom", "Student classroom", Instant.now(), Instant.now());
        MembershipJpaEntity studentMembership = new MembershipJpaEntity();
        studentMembership.setUserId(2L);
        studentMembership.setRole(ClassroomRole.STUDENT);
        studentMembership.setCreatedAt(Instant.now());
        studentMembership.setUpdatedAt(Instant.now());
        studentClassroom.addMember(studentMembership);

        classroomJpaRepository.save(teacherClassroom);
        classroomJpaRepository.save(studentClassroom);
        classroomJpaRepository.flush();

        int deleted = membershipJpaRepository.deleteByUserIdAndRole(2L, ClassroomRole.TEACHER);

        assertEquals(1, deleted);
        assertEquals(1L, membershipJpaRepository.count());
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

