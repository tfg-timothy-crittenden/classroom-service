package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomJpaRepository;
import com.timcritt.tfg.infrastructure.persistence.spring.MemberJpaRepository;
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
        "spring.cloud.config.enabled=false"
})
@Transactional
class MemberRepositoryAdapterTest {

    @Autowired
    private MemberRepositoryAdapter memberRepositoryAdapter;
    @Autowired
    private ClassroomJpaRepository classroomJpaRepository;
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void saveMember_persistsMemberAndAssociatesWithClassroom() {
        final ClassroomJpaEntity classroom = classroomJpaRepository.saveAndFlush(
                new ClassroomJpaEntity("TestClass", "desc", Instant.now(), Instant.now()));

        final Member member = new Member(null, 42L, "John", "Doe", ClassroomRole.STUDENT, Instant.now(), Instant.now());
        memberRepositoryAdapter.saveMember(classroom.getId(), member);

        // Verify member is persisted and associated
        final Optional<MemberJpaEntity> persisted = memberJpaRepository.findAll().stream()
                .filter(m -> m.getUserId().equals(42L) && m.getClassroom().getId().equals(classroom.getId()))
                .findFirst();
        assertTrue(persisted.isPresent());
        assertEquals("John", persisted.get().getName());
        assertEquals("Doe", persisted.get().getSurname());
        assertEquals(ClassroomRole.STUDENT, persisted.get().getRole());
    }

    @Test
    void saveMember_idempotentForSameMember() {
        final ClassroomJpaEntity classroom = classroomJpaRepository.saveAndFlush(
                new ClassroomJpaEntity("TestClass2", "desc", Instant.now(), Instant.now()));

        final Member member = new Member(null, 99L, "Alice", "Smith", ClassroomRole.STUDENT, Instant.now(), Instant.now());
        memberRepositoryAdapter.saveMember(classroom.getId(), member);
        // Try saving again (should throw due to unique constraint)
        assertThrows(Exception.class, () -> memberRepositoryAdapter.saveMember(classroom.getId(), member));
    }
}

