package com.timcritt.tfg.domain.model;

public class MaterialReference {

    private Long id;
    private Long materialId;
    private String name;
    private String description;
    private String part1Title;
    private String part2Title;
    private ClassroomRole assignedToRole;

    public MaterialReference() {
    }
    public MaterialReference(Long id, Long materialId, String name, String description, ClassroomRole assignedToRole) {
        this(id, materialId, name, description, null, null, assignedToRole);
    }
    public MaterialReference(Long id, Long materialId, String name, String description, String part1Title, String part2Title, ClassroomRole assignedToRole) {
        this.id = id;
        this.materialId = materialId;
        this.name = name;
        this.description = description;
        this.part1Title = part1Title;
        this.part2Title = part2Title;
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
    public String getPart1Title() {
        return part1Title;
    }
    public void setPart1Title(String part1Title) {
        this.part1Title = part1Title;
    }
    public String getPart2Title() {
        return part2Title;
    }
    public void setPart2Title(String part2Title) {
        this.part2Title = part2Title;
    }
    public ClassroomRole getAssignedToRole() {
        return assignedToRole;
    }
    public void setAssignedToRole(ClassroomRole assignedToRole) {
        this.assignedToRole = assignedToRole;
    }
}
