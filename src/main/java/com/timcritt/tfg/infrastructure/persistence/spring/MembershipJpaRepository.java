package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.jpa.MembershipJpaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MembershipJpaRepository extends JpaRepository<MembershipJpaEntity, Long> {

    @Query("SELECT m.role FROM MembershipJpaEntity m WHERE m.classroom.id = :classroomId AND m.userId = :userId")
    Optional<ClassroomRole> findRoleByClassroomIdAndUserId(
            @Param("classroomId") Long classroomId,
            @Param("userId") Long userId
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM MembershipJpaEntity m WHERE m.userId = :userId AND m.role = :role")
    int deleteByUserIdAndRole(@Param("userId") Long userId, @Param("role") ClassroomRole role);
}
