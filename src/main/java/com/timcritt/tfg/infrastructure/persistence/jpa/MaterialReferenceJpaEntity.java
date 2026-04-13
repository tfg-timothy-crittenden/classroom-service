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

    @Enumerated(EnumType.STRING)
    @Column(name = "assigned_to_role")
    private ClassroomRole assignedToRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassroomJpaEntity classroom;

    public MaterialReferenceJpaEntity() {
    }

    public MaterialReferenceJpaEntity(Long materialId, String name, String description, ClassroomRole assignedToRole) {
        this.materialId = materialId;
        this.name = name;
        this.description = description;
        this.assignedToRole = assignedToRole;
    }
}
