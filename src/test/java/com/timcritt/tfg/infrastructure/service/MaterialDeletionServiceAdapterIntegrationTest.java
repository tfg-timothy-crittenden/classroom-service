package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.MaterialDetailsRepositoryAdapter;
import com.timcritt.tfg.infrastructure.persistence.jpa.ClassroomJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialDetailsJpaEntity;
import com.timcritt.tfg.infrastructure.persistence.jpa.MaterialReferenceJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;

/** Uses the existing H2/JPA setup. Deliberately has no test-managed transaction. */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:material-deletion;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "spring.cloud.config.enabled=false",
        "classroom.grpc.enabled=false",
        "spring.kafka.listener.auto-startup=false",
        "eureka.client.enabled=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MaterialDeletionServiceAdapterIntegrationTest {

    private static final Long MATERIAL_ID = 26L;
    private static final Long OTHER_MATERIAL_ID = 27L;

    @Autowired
    private MaterialDeletionServiceAdapter deletionService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JdbcTemplate jdbc;

    @PersistenceContext
    private EntityManager entityManager;

    @MockitoSpyBean
    private MaterialDetailsRepositoryAdapter details;

    @BeforeEach
    void commitFixtures() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            entityManager.createQuery("delete from MaterialReferenceJpaEntity").executeUpdate();
            entityManager.createQuery("delete from MaterialDetailsJpaEntity").executeUpdate();
            entityManager.createQuery("delete from ClassroomJpaEntity").executeUpdate();
            for (int index = 0; index < 3; index++) {
                var classroom = new ClassroomJpaEntity("deletion-classroom-" + index, "", Instant.now(), Instant.now());
                entityManager.persist(classroom);
                persistReference(classroom, MATERIAL_ID);
                if (index == 0) {
                    persistReference(classroom, OTHER_MATERIAL_ID);
                }
            }
            entityManager.persist(new MaterialDetailsJpaEntity(MATERIAL_ID, 0L, "Material", null, null, null));
            entityManager.persist(new MaterialDetailsJpaEntity(OTHER_MATERIAL_ID, 0L, "Other", null, null, null));
        });
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertMaterialState(MATERIAL_ID, 3, 1);
    }

    @Test
    void rollsBackReferenceDeletionWhenDetailsDeletionFails() {
        var failure = new DataAccessResourceFailureException("forced details deletion failure");
        doAnswer(invocation -> {
            assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            // Real reference DELETE has already executed, not merely changed an in-memory mock.
            assertMaterialState(MATERIAL_ID, 0, 1);
            invocation.callRealMethod();
            entityManager.flush();
            assertMaterialState(MATERIAL_ID, 0, 0);
            throw failure;
        }).when(details).deleteByMaterialId(MATERIAL_ID);

        assertSame(failure, assertThrows(DataAccessResourceFailureException.class,
                () -> deletionService.handleMaterialDeleted(MATERIAL_ID)));

        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        // Fresh JDBC reads after the service transaction ended must see the committed fixtures.
        assertMaterialState(MATERIAL_ID, 3, 1);
        assertMaterialState(OTHER_MATERIAL_ID, 1, 1);
    }

    @Test
    void commitsBothDeletesAndDuplicateDeliverySucceedsWithoutChangingOtherMaterials() {
        assertDoesNotThrow(() -> deletionService.handleMaterialDeleted(MATERIAL_ID));
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        assertMaterialState(MATERIAL_ID, 0, 0);
        assertMaterialState(OTHER_MATERIAL_ID, 1, 1);

        assertDoesNotThrow(() -> deletionService.handleMaterialDeleted(MATERIAL_ID));
        assertMaterialState(MATERIAL_ID, 0, 0);
        assertMaterialState(OTHER_MATERIAL_ID, 1, 1);
    }

    private void persistReference(ClassroomJpaEntity classroom, Long materialId) {
        var reference = new MaterialReferenceJpaEntity(materialId, ClassroomRole.STUDENT);
        reference.setClassroom(classroom);
        entityManager.persist(reference);
    }

    private void assertMaterialState(Long materialId, int references, int projections) {
        assertEquals(references, jdbc.queryForObject(
                "select count(*) from material_reference where material_id = ?", Integer.class, materialId));
        assertEquals(projections, jdbc.queryForObject(
                "select count(*) from material_details where material_id = ?", Integer.class, materialId));
    }
}
