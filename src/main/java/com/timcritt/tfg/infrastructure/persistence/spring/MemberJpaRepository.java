package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberJpaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    @Query("SELECT m.role FROM MemberJpaEntity m WHERE m.classroom.id = :classroomId AND m.userId = :userId")
    Optional<ClassroomRole> findRoleByClassroomIdAndUserId(
            @Param("classroomId") Long classroomId,
            @Param("userId") Long userId
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM MemberJpaEntity m WHERE m.userId = :userId AND m.role = :role")
    int deleteByUserIdAndRole(@Param("userId") Long userId, @Param("role") ClassroomRole role);
}
