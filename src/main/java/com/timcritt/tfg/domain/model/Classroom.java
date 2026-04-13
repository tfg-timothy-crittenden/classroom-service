package com.timcritt.tfg.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Classroom {

    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    private List<com.timcritt.tfg.domain.model.Member> members = new ArrayList<>();
    private List<MaterialReference> materials = new ArrayList<>();

    public Classroom() {

    }

    public Classroom(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Classroom(Long id, String name, String description, List<Member> members, List<MaterialReference> materials) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members != null ? members : new ArrayList<>();
        this.materials = materials != null ? materials : new ArrayList<>();
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
    public List<com.timcritt.tfg.domain.model.Member> getMembers() {
        return members;
    }
    public void setMembers(List<com.timcritt.tfg.domain.model.Member> members) {
        this.members = members;
    }
    public List<MaterialReference> getMaterials() {
        return materials;
    }
    public void setMaterials(List<MaterialReference> materials) {
        this.materials = materials;
    }
    public void addMember(com.timcritt.tfg.domain.model.Member member) {
        members.add(member);
    }
    public void removeMember(com.timcritt.tfg.domain.model.Member member) {
        members.remove(member);
    }
    public void addMaterial(MaterialReference material) {
        materials.add(material);
    }
    public void removeMaterial(MaterialReference material) {
        materials.remove(material);
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
