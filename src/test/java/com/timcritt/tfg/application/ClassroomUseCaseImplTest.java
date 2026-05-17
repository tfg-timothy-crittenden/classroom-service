package com.timcritt.tfg.application;

import com.timcritt.tfg.application.port.outbound.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.application.service.ClassroomUseCaseImpl;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.Member;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClassroomUseCaseImplTest {

    private final Map<Long, Classroom> classrooms = new ConcurrentHashMap<>();
    private final InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();

    private final ClassroomRepositoryPort repository = new ClassroomRepositoryPort() {
        @Override
        public Classroom save(Classroom classroom) {
            classrooms.put(classroom.getId(), classroom);
            return classroom;
        }

        @Override
        public Classroom findById(Long id) {
            return classrooms.get(id);
        }

        @Override
        public void deleteById(Long id) {
            classrooms.remove(id);
        }

        @Override
        public void deleteByIds(List<Long> ids) {
            ids.forEach(classrooms::remove);
        }

        @Override
        public List<Classroom> findByMemberUserId(Long userId) {
            return classrooms.values().stream()
                    .filter(classroom -> classroom.getMembers().stream().anyMatch(member -> member.getUserId().equals(userId)))
                    .toList();
        }

        @Override
        public List<Classroom> findAll() {
            return List.copyOf(classrooms.values());
        }

        @Override
        public Classroom findByJoinCode(String joinCode) {
            return classrooms.values().stream()
                    .filter(classroom -> joinCode.equals(classroom.getJoinCode()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public boolean removeMemberFromClassroom(Long classroomId, Long userId) {
            Classroom classroom = classrooms.get(classroomId);
            if (classroom == null) {
                return false;
            }

            Member memberToRemove = classroom.getMembers().stream()
                    .filter(member -> member.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (memberToRemove == null) {
                return false;
            }

            classroom.removeMember(memberToRemove);
            return true;
        }

    };

    private final JoinCodeGenerator joinCodeGenerator = () -> "JOIN-123";
    private final ClassroomUseCaseImpl useCase = new ClassroomUseCaseImpl(repository, memberRepository, joinCodeGenerator);

    @Test
    void removesExistingMemberFromClassroom() {
        Classroom classroom = classroomWithMembers();
        classrooms.put(classroom.getId(), classroom);

        boolean removed = useCase.removeMemberFromClassroom(7L, 42L);

        assertTrue(removed);
        Classroom updated = classrooms.get(7L);
        assertNotNull(updated);
        assertFalse(updated.getMembers().stream().anyMatch(member -> member.getUserId().equals(42L)));
    }

    @Test
    void returnsFalseWhenMemberDoesNotExist() {
        Classroom classroom = classroomWithMembers();
        classrooms.put(classroom.getId(), classroom);

        boolean removed = useCase.removeMemberFromClassroom(7L, 999L);

        assertFalse(removed);
    }

    @Test
    void revokesTeacherRoleFromUserAcrossAllClassroomsAndKeepsStudentMemberships() {
        memberRepository.addMembership(7L, 42L, ClassroomRole.TEACHER);
        memberRepository.addMembership(8L, 42L, ClassroomRole.STUDENT);

        int updated = useCase.revokeTeacherRoleFromUser(42L);

        assertEquals(1, updated);
        assertFalse(memberRepository.hasMembership(7L, 42L, ClassroomRole.TEACHER));
        assertTrue(memberRepository.hasMembership(8L, 42L, ClassroomRole.STUDENT));

        assertEquals(0, useCase.revokeTeacherRoleFromUser(42L));
    }

    @Test
    void throwsStudentSpecificConflictWhenAssigningTeacherForExistingStudent() {
        Classroom classroom = classroomWithMembers();
        classrooms.put(classroom.getId(), classroom);

        ClassroomUseCaseImpl.MemberAlreadyInClassroomException exception = assertThrows(
                ClassroomUseCaseImpl.MemberAlreadyInClassroomException.class,
                () -> useCase.assignTeacherToClassroom(
                        7L,
                        new Member(null, 42L, "John", "Smith", ClassroomRole.TEACHER, Instant.now(), Instant.now())
                )
        );

        assertEquals("John Smith is already a member of Math", exception.getMessage());
    }

    @Test
    void throwsStudentSpecificConflictWhenJoiningAlreadyJoinedClassroom() {
        Classroom classroom = classroomWithMembers();
        classrooms.put(classroom.getId(), classroom);

        ClassroomUseCaseImpl.MemberAlreadyInClassroomException exception = assertThrows(
                ClassroomUseCaseImpl.MemberAlreadyInClassroomException.class,
                () -> useCase.joinClassroom(42L, "JOIN-123", "John", "Smith")
        );

        assertEquals("John Smith is already a member of Math", exception.getMessage());
    }

    @Test
    void throwsStudentSpecificConflictWhenSyncingTeachersForExistingStudent() {
        Classroom classroom = classroomWithMembers();
        classrooms.put(classroom.getId(), classroom);

        ClassroomUseCaseImpl.MemberAlreadyInClassroomException exception = assertThrows(
                ClassroomUseCaseImpl.MemberAlreadyInClassroomException.class,
                () -> useCase.syncTeachersForClassroom(
                        7L,
                        List.of(new Member(null, 42L, "John", "Smith", ClassroomRole.TEACHER, Instant.now(), Instant.now()))
                )
        );

        assertEquals("John Smith is already a member of Math", exception.getMessage());
    }

    private Classroom classroomWithMembers() {
        Classroom classroom = new Classroom(7L, "Math", "Math class");
        classroom.setJoinCode("JOIN-123");
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());

        Member teacher = new Member(null, 43L, "Jane", "Doe", ClassroomRole.TEACHER, Instant.now(), Instant.now());
        Member student = new Member(null, 42L, "John", "Smith", ClassroomRole.STUDENT, Instant.now(), Instant.now());
        classroom.addMember(teacher);
        classroom.addMember(student);
        return classroom;
    }

    private Classroom classroomWithTeacher() {
        Classroom classroom = new Classroom(7L, "Teacher class 7", "Teacher class");
        classroom.setJoinCode("JOIN-7");
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());

        Member teacher = new Member(null, 42L, "Jane", "Doe", ClassroomRole.TEACHER, Instant.now(), Instant.now());
        classroom.addMember(teacher);
        return classroom;
    }

    private Classroom classroomWithStudent() {
        Classroom classroom = new Classroom(8L, "Student class 8", "Student class");
        classroom.setJoinCode("JOIN-8");
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());

        Member student = new Member(null, 42L, "John", "Smith", ClassroomRole.STUDENT, Instant.now(), Instant.now());
        classroom.addMember(student);
        return classroom;
    }

    private static class InMemoryMemberRepository implements MemberRepositoryPort {
        private final Map<Key, ClassroomRole> memberships = new HashMap<>();

        void addMembership(Long classroomId, Long userId, ClassroomRole role) {
            memberships.put(new Key(classroomId, userId), role);
        }

        boolean hasMembership(Long classroomId, Long userId, ClassroomRole role) {
            return role == memberships.get(new Key(classroomId, userId));
        }

        @Override
        public Optional<ClassroomRole> findRoleByClassroomIdAndUserId(Long classroomId, Long userId) {
            return Optional.ofNullable(memberships.get(new Key(classroomId, userId)));
        }

        @Override
        public int deleteTeacherMembershipsByUserId(Long userId) {
            int removed = 0;
            var iterator = memberships.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Key, ClassroomRole> entry = iterator.next();
                if (entry.getKey().userId.equals(userId) && entry.getValue() == ClassroomRole.TEACHER) {
                    iterator.remove();
                    removed++;
                }
            }
            return removed;
        }

        @Override
        public void saveMember(Long classroomId, Member member) {
            memberships.put(new Key(classroomId, member.getUserId()), member.getRole());
        }

        private record Key(Long classroomId, Long userId) { }
    }
}

