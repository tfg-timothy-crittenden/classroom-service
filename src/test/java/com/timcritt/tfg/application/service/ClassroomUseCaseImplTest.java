package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.outbound.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.Member;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassroomUseCaseImplTest {

    private final Map<Long, Classroom> classrooms = new ConcurrentHashMap<>();

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
    private final ClassroomUseCaseImpl useCase = new ClassroomUseCaseImpl(repository, joinCodeGenerator);

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
}

