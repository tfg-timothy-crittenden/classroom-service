package com.timcritt.tfg.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="member_profile")
@Getter
@Setter
public class MemberProfileJpaEntity {

    @Id
    private Long userId;

    @Column(nullable=false)
    private Long version;

    @Column @NotNull @NotEmpty
    private String firstName;

    @Column @NotNull @NotEmpty
    private String lastName;

    public MemberProfileJpaEntity() {
    }

    public MemberProfileJpaEntity(Long userId, Long version, String firstName, String lastName) {
        this.userId = userId;
        this.version = version;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
