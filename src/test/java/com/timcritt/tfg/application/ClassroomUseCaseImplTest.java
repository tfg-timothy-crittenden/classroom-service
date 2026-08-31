package com.timcritt.tfg.application;

import com.timcritt.tfg.application.exception.ClassroomNotFoundException;
import com.timcritt.tfg.application.port.outbound.*;
import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MembershipRepositoryPort;
import com.timcritt.tfg.domain.exception.MemberAlreadyInClassroomException;
import com.timcritt.tfg.domain.exception.TeacherAlreadyAssignedException;
import com.timcritt.tfg.application.service.useCase.ClassroomManagementUseCaseImpl;
import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.domain.aggregate.classroom.MaterialReference;
import com.timcritt.tfg.domain.aggregate.classroom.Membership;
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
    private final InMemoryMembershipRepository memberRepository = new InMemoryMembershipRepository();
    private final InMemoryMaterialReferenceRepository materialReferenceRepository = new InMemoryMaterialReferenceRepository() {
        @Override
        public List<MaterialReference> findByMaterialId(Long id) {
            return List.of();
        }
    };

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
                    .filter(classroom -> classroom.getMembers().containsKey(userId))
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
    };

    private final JoinCodeGenerator joinCodeGenerator = () -> "JOIN-123";
    private final ClassroomManagementUseCaseImpl useCase = new ClassroomManagementUseCaseImpl(repository, memberRepository, joinCodeGenerator, materialReferenceRepository);

    @Test
    void removesExistingMemberFromClassroom() {
        Classroom classroom = classroomWithMembers();
        classrooms.put(classroom.getId(), classroom);

       useCase.removeMemberFromClassroom(7L, 42L);


        Classroom updated = classrooms.get(7L);
        assertNotNull(updated);
        assertFalse(updated.getMembers().containsKey(42L));
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

        MemberAlreadyInClassroomException exception = assertThrows(
                MemberAlreadyInClassroomException.class,
                () -> useCase.assignTeacherToClassroom(7L, 42L, "John", "Smith")
        );

        assertEquals("John Smith is already a student in Math", exception.getMessage());
    }

    @Test
    void throwsStudentSpecificConflictWhenJoiningAlreadyJoinedClassroom() {
        Classroom classroom = classroomWithMembers();
        classrooms.put(classroom.getId(), classroom);

        MemberAlreadyInClassroomException exception = assertThrows(
                MemberAlreadyInClassroomException.class,
                () -> useCase.joinClassroom(42L, "JOIN-123", "John", "Smith")
        );

        assertEquals("John Smith is already a student in Math", exception.getMessage());
    }

    @Test
    void throwsStudentSpecificConflictWhenSyncingTeachersForExistingStudent() {
        Classroom classroom = classroomWithMembers();
        classrooms.put(classroom.getId(), classroom);

        MemberAlreadyInClassroomException exception = assertThrows(
                MemberAlreadyInClassroomException.class,
                () -> useCase.syncTeachersForClassroom(
                        7L,
                        List.of(new Membership(null, 42L, ClassroomRole.TEACHER, Instant.now(), Instant.now()))
                )
        );

        assertEquals("John Smith is already a student in Math", exception.getMessage());
    }

    // ── assignTeacherToClassroom ──────────────────────────────────────────────

    @Test
    void assignTeacher_successfullyAddsTeacherToClassroom() {
        Classroom classroom = new Classroom(7L, "Math", "Math class");
        classrooms.put(classroom.getId(), classroom);

        Membership newTeacher = new Membership(null, 99L, ClassroomRole.TEACHER, Instant.now(), Instant.now());
        Classroom updated = useCase.assignTeacherToClassroom(7L, 99L, "Alice", "Brown");

        assertTrue(updated.getMembers().containsKey(99L));
    }

    @Test
    void assignTeacher_throwsClassroomNotFoundWhenClassroomMissing() {
        assertThrows(
                ClassroomNotFoundException.class,
                () -> useCase.assignTeacherToClassroom(999L, 1L, "X", "Y")
        );
    }

    @Test
    void assignTeacher_throwsTeacherAlreadyAssignedWhenTeacherAlreadyInClassroom() {
        Classroom classroom = classroomWithTeacher(); // has teacher userId=42
        classrooms.put(classroom.getId(), classroom);

        assertThrows(
                TeacherAlreadyAssignedException.class,
                () -> useCase.assignTeacherToClassroom(7L, 42L, "Jane", "Doe")
        );
    }

    // ── joinClassroom ─────────────────────────────────────────────────────────

    @Test
    void joinClassroom_successfullyAddsStudentMembership() {
        Classroom classroom = new Classroom(7L, "Math", "Math class");
        classroom.setJoinCode("JOIN-123");
        classrooms.put(classroom.getId(), classroom);

        useCase.joinClassroom(55L, "JOIN-123", "New", "Student");

        Classroom updated = classrooms.get(7L);
        assertTrue(updated.getMembers().containsKey(55L));
        assertEquals(ClassroomRole.STUDENT, updated.getMembers().get(55L).getRole());
    }

    @Test
    void joinClassroom_throwsClassroomNotFoundWhenJoinCodeInvalid() {
        assertThrows(
                ClassroomNotFoundException.class,
                () -> useCase.joinClassroom(1L, "BAD-CODE", "X", "Y")
        );
    }

    // ── syncTeachersForClassroom ──────────────────────────────────────────────

    @Test
    void syncTeachers_throwsClassroomNotFoundWhenClassroomMissing() {
        assertThrows(
                ClassroomNotFoundException.class,
                () -> useCase.syncTeachersForClassroom(999L, List.of())
        );
    }

    @Test
    void syncTeachers_removesTeachersNotInNewList() {
        Classroom classroom = classroomWithTeacher(); // teacher userId=42
        classrooms.put(classroom.getId(), classroom);

        useCase.syncTeachersForClassroom(7L, List.of()); // empty list → remove all teachers

        Classroom updated = classrooms.get(7L);
        assertTrue(updated.getMembers().values().stream().noneMatch(m -> m.getRole() == ClassroomRole.TEACHER));
    }

    @Test
    void syncTeachers_addsNewTeachersNotCurrentlyInClassroom() {
        Classroom classroom = new Classroom(7L, "Math", "Math class");
        classrooms.put(classroom.getId(), classroom);

        Membership newTeacher = new Membership(null, 77L, ClassroomRole.TEACHER, Instant.now(), Instant.now());
        useCase.syncTeachersForClassroom(7L, List.of(newTeacher));

        Classroom updated = classrooms.get(7L);
        assertTrue(updated.getMembers().containsKey(77L));
    }



    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    void save_generatesJoinCodeWhenAbsent() {
        Classroom classroom = new Classroom(10L, "No Code", "desc");
        classrooms.put(classroom.getId(), classroom);

        Classroom saved = useCase.save(classroom);

        assertEquals("JOIN-123", saved.getJoinCode());
    }

    @Test
    void save_keepsExistingJoinCodeWhenPresent() {
        Classroom classroom = new Classroom(10L, "Has Code", "desc");
        classroom.setJoinCode("EXISTING-CODE");
        classrooms.put(classroom.getId(), classroom);

        Classroom saved = useCase.save(classroom);

        assertEquals("EXISTING-CODE", saved.getJoinCode());
    }

    // ── getMembersByRole ──────────────────────────────────────────────────────

    @Test
    void getMembersByRole_returnsOnlyMembersWithMatchingRole() {
        Classroom classroom = classroomWithMembers(); // 1 teacher (43), 1 student (42)
        classrooms.put(classroom.getId(), classroom);

        List<Membership> students = useCase.getMembersByRole(7L, ClassroomRole.STUDENT);
        List<Membership> teachers = useCase.getMembersByRole(7L, ClassroomRole.TEACHER);

        assertEquals(1, students.size());
        assertEquals(42L, students.getFirst().getUserId());
        assertEquals(1, teachers.size());
        assertEquals(43L, teachers.getFirst().getUserId());
    }

    @Test
    void getMembersByRole_throwsWhenClassroomNotFound() {
        assertThrows(ClassroomNotFoundException.class, () -> useCase.getMembersByRole(999L, ClassroomRole.STUDENT));
    }

    // ── revokeTeacherRoleFromUser ─────────────────────────────────────────────

    @Test
    void revokeTeacherRole_throwsWhenUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> useCase.revokeTeacherRoleFromUser(null));
    }

    private Classroom classroomWithMembers() {
        Classroom classroom = new Classroom(7L, "Math", "Math class");
        classroom.setJoinCode("JOIN-123");
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());

        Membership teacher = new Membership(null, 43L,  ClassroomRole.TEACHER, Instant.now(), Instant.now());
        Membership student = new Membership(null, 42L, ClassroomRole.STUDENT, Instant.now(), Instant.now());
        classroom.addMember(teacher);
        classroom.addMember(student);
        return classroom;
    }

    private Classroom classroomWithTeacher() {
        Classroom classroom = new Classroom(7L, "Teacher class 7", "Teacher class");
        classroom.setJoinCode("JOIN-7");
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());

        Membership teacher = new Membership(null, 42L, ClassroomRole.TEACHER, Instant.now(), Instant.now());
        classroom.addMember(teacher);
        return classroom;
    }

    private Classroom classroomWithStudent() {
        Classroom classroom = new Classroom(8L, "Student class 8", "Student class");
        classroom.setJoinCode("JOIN-8");
        classroom.setCreatedAt(Instant.now());
        classroom.setUpdatedAt(Instant.now());

        Membership student = new Membership(null, 42L, ClassroomRole.STUDENT, Instant.now(), Instant.now());
        classroom.addMember(student);
        return classroom;
    }

    private static class InMemoryMembershipRepository implements MembershipRepositoryPort {
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
        public void saveMember(Long classroomId, Membership membership) {
            memberships.put(new Key(classroomId, membership.getUserId()), membership.getRole());
        }

        private record Key(Long classroomId, Long userId) { }
    }

//    TODO implement the actual in-memory repository with real methods that return something. Do it somewhere centally so we can reuse it.
    private static abstract class InMemoryMaterialReferenceRepository implements MaterialReferenceRepositoryPort {


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
    public int deleteByMaterialId(Long materialId) {
        return 0;
    }

    @Override
    public void save(MaterialReference materialReference) {

    }


}
}

