package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.domain.projection.MemberProfile;
import com.timcritt.tfg.infrastructure.persistence.jpa.MemberProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileJpaRepository extends JpaRepository<MemberProfileJpaEntity, Long> {

}
