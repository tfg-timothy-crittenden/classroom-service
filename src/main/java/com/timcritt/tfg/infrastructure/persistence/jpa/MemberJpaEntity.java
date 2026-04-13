package com.timcritt.tfg.infrastructure.persistence.jpa;

import com.timcritt.tfg.domain.model.ClassroomRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity @Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_classroom_user",
                        columnNames = {"classroom_id", "user_id"}
                )
        }
)
@Getter
@Setter
public class MemberJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassroomRole role;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassroomJpaEntity classroom;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

}
