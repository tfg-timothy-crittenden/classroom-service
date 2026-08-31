package com.timcritt.tfg.domain.aggregate.classroom;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.timcritt.tfg.domain.exception.MemberNotFoundException;
import com.timcritt.tfg.domain.exception.MemberAlreadyInClassroomException;
import com.timcritt.tfg.domain.exception.TeacherAlreadyAssignedException;

public class Classroom {

    private Long id;
    private String name;
    private String description;
    private String joinCode;
    private Instant createdAt;
    private Instant updatedAt;

    private Map<Long, Membership> members = new HashMap<>() {
    };

    private List<MaterialReference> materials = new ArrayList<>();

    public Classroom() {
    }

    public Classroom(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Classroom(Long id, String name, String description, String joinCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.joinCode = joinCode;
    }

    public Classroom(Long id, String name, String description, String joinCode, Map<Long, Membership> members, List<MaterialReference> materials) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.joinCode = joinCode;
        this.members = members != null ? members : new HashMap<>();
        this.materials = materials != null ? materials : new ArrayList<>();
    }

    public void assignTeacher(Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        assignMember(userId, ClassroomRole.TEACHER);
    }

    public void assignStudent(Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        assignMember(userId, ClassroomRole.STUDENT);
    }

    public void syncTeachers(List<Membership> newTeachers) {
        Objects.requireNonNull(newTeachers, "newTeachers cannot be null");

        Set<Long> newTeacherIds = newTeachers.stream()
                .peek(teacher -> Objects.requireNonNull(teacher, "teacher cannot be null"))
                .map(Membership::getUserId)
                .peek(userId -> Objects.requireNonNull(userId, "teacher.userId cannot be null"))
                .collect(Collectors.toSet());

        // Remove existing teachers not present in the incoming teacher set.
        members.entrySet().removeIf(entry ->
                entry.getValue().getRole() == ClassroomRole.TEACHER
                        && !newTeacherIds.contains(entry.getKey())
        );

        // Add only missing teachers; do not mutate existing teachers.
        for (Membership teacher : newTeachers) {
            Membership existing = members.get(teacher.getUserId());
            if (existing == null) {
                assignTeacher(teacher.getUserId());
                continue;
            }

            //Don't promote students to teachers
            if (existing.getRole() == ClassroomRole.STUDENT) {
                throw new MemberAlreadyInClassroomException(
                        "User is already a student in " + this.name
                );
            }
        }
    }

    public List<Membership> getMembersByRole(ClassroomRole role) {
        Objects.requireNonNull(role, "role cannot be null");
        return this.members.values().stream().filter(member ->
                member.getRole() == role).collect(Collectors.toList());
    }

    public void removeMember(Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Membership existing = this.getMemberById(userId);
        if (existing == null) {
            throw new MemberNotFoundException("Membership not found in classroom");
        }
        members.remove(existing.getUserId());
    }

    public Membership getMemberById(Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Membership membership = members.get(userId);
        if (membership == null) {
            throw new MemberNotFoundException("Membership not found in classroom");
        }
        return membership;
    }

//  Auxiliary Methods
    private void assignMember(Long userId, ClassroomRole role) {
        Membership existing = members.get(userId);

        if (existing == null) {
            Instant now = Instant.now();
            members.put(userId, new Membership(null, userId, role, now, now));
            return;
        }

        if (existing.getRole() == ClassroomRole.STUDENT) {
            throw new MemberAlreadyInClassroomException(
                    "User is already a student in " + this.name
            );
        }
        throw new TeacherAlreadyAssignedException(
                "User is already a teacher in " + this.name
        );
    }

    public void replaceMaterials(List<MaterialReference> newMaterials) {
        if (newMaterials == null) {
            throw new IllegalArgumentException("newMaterials cannot be null");
        }
        this.materials = newMaterials;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Map<Long, Membership> getMembers() {
        return members;
    }
    public void setMembers(Map<Long, Membership> members) {
        this.members = members;
    }
    public List<MaterialReference> getMaterials() {
        return materials;
    }
    public void setMaterials(List<MaterialReference> materials) {
        this.materials = materials;
    }
    public void addMember(Membership membership) {
        members.put(membership.getUserId(), membership);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}
