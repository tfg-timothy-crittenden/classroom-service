package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialDetailsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialDetailsJpaRepository extends JpaRepository<MaterialDetailsJpaEntity, Long> {

}
