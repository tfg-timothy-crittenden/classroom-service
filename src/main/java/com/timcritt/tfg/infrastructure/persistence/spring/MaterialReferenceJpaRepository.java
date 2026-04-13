package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialReferenceJpaRepository extends JpaRepository<MaterialReferenceJpaEntity, Long> {
    @Query("SELECT m FROM MaterialReferenceJpaEntity m WHERE m.classroom.id = :classroomId")
    List<MaterialReferenceJpaEntity> findByClassroomId(@Param("classroomId") Long classroomId);

    @Query("SELECT m FROM MaterialReferenceJpaEntity m WHERE m.classroom.id = :classroomId AND m.assignedToRole = :role")
    List<MaterialReferenceJpaEntity> findByClassroomIdAndAssignedToRole(
            @Param("classroomId") Long classroomId,
            @Param("role") ClassroomRole role
    );
}
