package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassroomJpaRepository extends JpaRepository<ClassroomJpaEntity, Long> {

    @Query("SELECT c FROM ClassroomJpaEntity c JOIN c.members m WHERE m.userId = :userId")
    List<ClassroomJpaEntity> findByMemberUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM ClassroomJpaEntity c LEFT JOIN FETCH c.materials WHERE c.id = :id")
    Optional<ClassroomJpaEntity> findByIdWithMaterials(@Param("id") Long id);
}
