package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT m FROM MaterialReferenceJpaEntity m JOIN FETCH m.classroom WHERE m.materialId = :materialId")
    List<MaterialReferenceJpaEntity> findByMaterialId(@Param("materialId") Long materialId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM MaterialReferenceJpaEntity m WHERE m.materialId = :materialId")
    int deleteByMaterialId(@Param("materialId") Long materialId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE MaterialReferenceJpaEntity m SET " +
            "m.name = CASE WHEN :title IS NULL THEN m.name ELSE :title END, " +
            "m.part1Title = CASE WHEN :part1Title IS NULL THEN m.part1Title ELSE :part1Title END, " +
            "m.part2Title = CASE WHEN :part2Title IS NULL THEN m.part2Title ELSE :part2Title END " +
            "WHERE m.materialId = :materialId")
    int updateTitlesByMaterialId(
            @Param("materialId") Long materialId,
            @Param("title") String title,
            @Param("part1Title") String part1Title,
            @Param("part2Title") String part2Title
    );
}
