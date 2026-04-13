package com.timcritt.tfg.domain.model;

import java.time.Instant;

public class Member {

    private Long id;
    private Long userId;

    private String name;
    private String surname;

    private ClassroomRole role;

    private Instant createdAt;
    private Instant updatedAt;


    public Member() {}

    public Member(Long id, Long userId, String name, String surname, ClassroomRole role, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public ClassroomRole getRole() {
        return role;
    }
    public void setRole(ClassroomRole role) {
        this.role = role;
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
