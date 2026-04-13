package com.timcritt.tfg.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "classroom")
@Getter
@Setter
public class ClassroomJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "classroom",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MemberJpaEntity> members = new ArrayList<>();

    @OneToMany(
            mappedBy = "classroom",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MaterialReferenceJpaEntity> materials = new ArrayList<>();


    public ClassroomJpaEntity() {

    }
    public ClassroomJpaEntity(String name, String description, Instant createdAt, Instant updatedAt) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public void addMember(MemberJpaEntity member) {
        members.add(member);
        member.setClassroom(this);
    }
    public void removeMember(MemberJpaEntity member) {
        members.remove(member);
        member.setClassroom(null);
    }

    public void addMaterial(MaterialReferenceJpaEntity material) {
        materials.add(material);
        material.setClassroom(this);
    }
    public void removeMaterial(MaterialReferenceJpaEntity material) {
        materials.remove(material);
        material.setClassroom(null);
    }
}
