package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MembershipJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import com.timcritt.tfg.infrastructure.persistence.spring.MembershipJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
class MembershipRepositoryAdapterTest {

    @Autowired
    private MembershipRepositoryAdapter memberRepositoryAdapter;
    @Autowired
    private ClassroomJpaRepository classroomJpaRepository;
    @Autowired
    private MembershipJpaRepository membershipJpaRepository;

    @Test
    void saveMember_persistsMemberAndAssociatesWithClassroom() {
        final ClassroomJpaEntity classroom = classroomJpaRepository.saveAndFlush(
                new ClassroomJpaEntity("TestClass", "desc", Instant.now(), Instant.now()));

        final Membership membership = new Membership(null, 42L,ClassroomRole.STUDENT, Instant.now(), Instant.now());
        memberRepositoryAdapter.saveMember(classroom.getId(), membership);

        // Verify membership is persisted and associated
        final Optional<MembershipJpaEntity> persisted = membershipJpaRepository.findAll().stream()
                .filter(m -> m.getUserId().equals(42L) && m.getClassroom().getId().equals(classroom.getId()))
                .findFirst();
        assertTrue(persisted.isPresent());

        assertEquals(ClassroomRole.STUDENT, persisted.get().getRole());
    }

    @Test
    void saveMember_idempotentForSameMember() {
        final ClassroomJpaEntity classroom = classroomJpaRepository.saveAndFlush(
                new ClassroomJpaEntity("TestClass2", "desc", Instant.now(), Instant.now()));

        final Membership membership = new Membership(null, 99L, ClassroomRole.STUDENT, Instant.now(), Instant.now());
        memberRepositoryAdapter.saveMember(classroom.getId(), membership);
        // Try saving again (should throw due to unique constraint)
        assertThrows(Exception.class, () -> memberRepositoryAdapter.saveMember(classroom.getId(), membership));
    }
}

