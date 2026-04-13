package com.timcritt.tfg.domain.model;

public class MaterialReference {

    private Long id;
    private Long materialId;
    private String name;
    private String description;
    private ClassroomRole assignedToRole;

    public MaterialReference() {
    }
    public MaterialReference(Long id, Long materialId, String name, String description, ClassroomRole assignedToRole) {
        this.id = id;
        this.materialId = materialId;
        this.name = name;
        this.description = description;
        this.assignedToRole = assignedToRole;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMaterialId() {
        return materialId;
    }
    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
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
    public ClassroomRole getAssignedToRole() {
        return assignedToRole;
    }
    public void setAssignedToRole(ClassroomRole assignedToRole) {
        this.assignedToRole = assignedToRole;
    }
}
