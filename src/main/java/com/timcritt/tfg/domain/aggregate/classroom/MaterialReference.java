package com.timcritt.tfg.domain.aggregate.classroom;

public class MaterialReference {

    private Long id;
    private Long materialId;
    private ClassroomRole assignedToRole;

    public MaterialReference() {
    }

    public MaterialReference(Long id, Long materialId, ClassroomRole assignedToRole) {
        this.id = id;
        this.materialId = materialId;
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
    public ClassroomRole getAssignedToRole() {
        return assignedToRole;
    }
    public void setAssignedToRole(ClassroomRole assignedToRole) {
        this.assignedToRole = assignedToRole;
    }
}
