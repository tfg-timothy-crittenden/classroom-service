package com.timcritt.tfg.infrastructure.persistence.jpa;

import com.timcritt.tfg.domain.model.ClassroomRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "material_reference")
@Getter
@Setter
public class MaterialReferenceJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "part1_title")
    private String part1Title;

    @Column(name = "part2_title")
    private String part2Title;

    @Enumerated(EnumType.STRING)
    @Column(name = "assigned_to_role")
    private ClassroomRole assignedToRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassroomJpaEntity classroom;

    public MaterialReferenceJpaEntity() {
    }

    public MaterialReferenceJpaEntity(Long materialId, String name, String description, ClassroomRole assignedToRole) {
        this(materialId, name, description, null, null, assignedToRole);
    }

    public MaterialReferenceJpaEntity(Long materialId, String name, String description, String part1Title, String part2Title, ClassroomRole assignedToRole) {
        this.materialId = materialId;
        this.name = name;
        this.description = description;
        this.part1Title = part1Title;
        this.part2Title = part2Title;
        this.assignedToRole = assignedToRole;
    }
}
