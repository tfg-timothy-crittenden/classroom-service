package com.timcritt.tfg.infrastructure.persistence.spring;

import com.timcritt.tfg.infrastructure.persistence.jpa.IntegrationOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationOutboxJpaRepository extends JpaRepository<IntegrationOutboxJpaEntity, Long> {
}

