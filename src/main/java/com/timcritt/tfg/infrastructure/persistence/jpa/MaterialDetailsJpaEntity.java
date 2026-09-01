package com.timcritt.tfg.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="material_details")
@Getter
@Setter
public class MaterialDetailsJpaEntity {

    @Id
    private Long materialId;

    @Column(nullable = false)
    private Long version;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String part1Title;

    @Column
    private String part2Title;

    public MaterialDetailsJpaEntity() {}

    public MaterialDetailsJpaEntity(Long materialId, Long version, String name, String description, String part1Title, String part2Title) {
        this.materialId = materialId;
        this.version = version;
        this.name = name;
        this.description = description;
        this.part1Title = part1Title;
        this.part2Title = part2Title;

    }

}
