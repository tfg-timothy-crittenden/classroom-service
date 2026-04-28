package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.inbound.ClassroomUseCase;
import com.timcritt.tfg.application.port.outbound.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.domain.model.ClassroomRole;

import java.util.List;

public class ClassroomUseCaseImpl implements ClassroomUseCase {

    private final ClassroomRepositoryPort repository;
    private final JoinCodeGenerator joinCodeGenerator;

    public ClassroomUseCaseImpl(ClassroomRepositoryPort repository, JoinCodeGenerator joinCodeGenerator) {
        this.repository = repository;
        this.joinCodeGenerator = joinCodeGenerator;
    }

    @Override
    public List<Classroom> getClassroomsByMember(Long userId) {
        return repository.findByMemberUserId(userId);
    }

    @Override
    public List<Classroom> getAllClassrooms() {
        return repository.findAll();
    }

    @Override
    public Classroom save(Classroom classroom) {
        if (classroom.getJoinCode() == null || classroom.getJoinCode().isEmpty()) {
            classroom.setJoinCode(joinCodeGenerator.generateJoinCode());
        }
        return repository.save(classroom);
    }

    @Override
    public Classroom assignTeacherToClassroom(Long classroomId, Member member) {
        Classroom classroom = repository.findById(classroomId);
        if (classroom == null) {
            throw new IllegalArgumentException("Classroom not found: " + classroomId);
        }
        // Check if a teacher with the same userId already exists
        boolean alreadyAssigned = classroom.getMembers().stream()
            .anyMatch(m -> m.getUserId().equals(member.getUserId()) && m.getRole() == ClassroomRole.TEACHER);
        if (alreadyAssigned) {
            throw new TeacherAlreadyAssignedException("Teacher with userId " + member.getUserId() + " is already assigned to classroom " + classroomId);
        }
        classroom.addMember(member);
        return repository.save(classroom);
    }

    @Override
    public void deleteClassroomById(Long classroomId) {
        repository.deleteById(classroomId);
    }

    // Add this method for syncTeachersForClassroom
    public Classroom getClassroomById(Long classroomId) {
        return repository.findById(classroomId);
    }

    @Override
    public void deleteClassroomsByIds(List<Long> classroomIds) {
        repository.deleteByIds(classroomIds);
    }

    // Custom exception for duplicate teacher assignment
    public static class TeacherAlreadyAssignedException extends RuntimeException {
        public TeacherAlreadyAssignedException(String message) {
            super(message);
        }
    }

    @Override
    public List<Member> getMembersByRole(Long classroomId, ClassroomRole role) {
        Classroom classroom = repository.findById(classroomId);
        if (classroom == null || classroom.getMembers() == null) {
            return java.util.Collections.emptyList();
        }
        return classroom.getMembers().stream()
                .filter(member -> member.getRole() == role)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Classroom syncTeachersForClassroom(Long classroomId, List<Member> teachers) {
        Classroom classroom = repository.findById(classroomId);
        if (classroom == null) {
            throw new IllegalArgumentException("Classroom not found: " + classroomId);
        }
        List<Member> currentTeachers = classroom.getMembers().stream()
                .filter(m -> m.getRole() == ClassroomRole.TEACHER)
                .toList();
        // Remove teachers not in the new list
        currentTeachers.stream()
                .filter(m -> teachers.stream().noneMatch(t -> t.getUserId().equals(m.getUserId())))
                .forEach(classroom::removeMember);
        // Add or update teachers
        teachers.forEach(teacher -> {
            Member existing = currentTeachers.stream()
                .filter(m -> m.getUserId().equals(teacher.getUserId()))
                .findFirst().orElse(null);
            if (existing == null) {
                classroom.addMember(teacher);
            } else {
                existing.setName(teacher.getName());
                existing.setSurname(teacher.getSurname());
                existing.setUpdatedAt(java.time.Instant.now());
            }
        });
        return repository.save(classroom);
    }

    @Override
    public Classroom joinClassroom(Long userId, String classCode, String name, String surname) {
        // Find classroom by join code
        Classroom classroom = repository.findByJoinCode(classCode);
        if (classroom == null) {
            throw new IllegalArgumentException("Classroom not found for code: " + classCode);
        }
        // Check if user is already a member
        boolean alreadyMember = classroom.getMembers().stream()
            .anyMatch(m -> m.getUserId().equals(userId));
        if (alreadyMember) {
            //todo throw error
            return classroom; // Or throw if you want to prevent re-joining
        }
        // Add as student
        Member member = new Member();
        member.setUserId(userId);
        member.setName(name);
        member.setSurname(surname);
        member.setRole(ClassroomRole.STUDENT);
        member.setCreatedAt(java.time.Instant.now());
        member.setUpdatedAt(java.time.Instant.now());
        classroom.addMember(member);
        return repository.save(classroom);
    }

    @Override
    public boolean removeMemberFromClassroom(Long classroomId, Long userId) {
        return repository.removeMemberFromClassroom(classroomId, userId);
    }
}
