package com.timcritt.tfg.infrastructure.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import static com.timcritt.tfg.infrastructure.kafka.ClassroomIntegrationEventFailureContractTest.FACTORY;
import static com.timcritt.tfg.infrastructure.kafka.ClassroomIntegrationEventFailureContractTest.INVALID_EVENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Application-owned API: a no-arg configuration with
 * FixedBackOff, a named DLT resolver, DeadLetterPublishingRecoverer, DefaultErrorHandler,
 * and the named container factory as beans. Its public classroomIntegrationErrorHandler(
 * ConsumerRecordRecoverer, FixedBackOff) bean method permits a recording recoverer and
 * zero-delay backoff in unit tests. Reflection is restricted to these application
 * types; all Kafka interaction uses public APIs. No application scan or broker is started.
 */
class ClassroomIntegrationKafkaConfigurationContractTest {

    private static final String CONFIGURATION = "com.timcritt.tfg.infrastructure.kafka.ClassroomIntegrationKafkaConfiguration";
    private static final String RESOLVER = "classroomIntegrationDltDestinationResolver";

    @Test
    void dedicatedFactoryUsesConfiguredConsumerFactoryAndErrorHandler() {
        try (var context = context()) {
            var factory = context.getBean(FACTORY, ConcurrentKafkaListenerContainerFactory.class);
            var handler = context.getBean(DefaultErrorHandler.class);
            assertSame(context.getBean(ConsumerFactory.class), factory.getConsumerFactory());
            // Creating, but never starting, a container exercises factory wiring without Kafka.
            var container = factory.createContainer("material.deleted.v1");
            assertSame(handler, container.getCommonErrorHandler());
            assertEquals(ContainerProperties.AckMode.RECORD, container.getContainerProperties().getAckMode(),
                    "Boot's configurer must apply listener properties");
            assertNotNull(context.getBean(DeadLetterPublishingRecoverer.class));
        }
    }

    @Test
    void bootDefaultFactoryDoesNotUseIntegrationErrorHandler() {
        try (var context = context()) {
            var defaultFactory = context.getBean("kafkaListenerContainerFactory", ConcurrentKafkaListenerContainerFactory.class);
            assertNotSame(context.getBean(FACTORY), defaultFactory);
            assertNotSame(context.getBean(DefaultErrorHandler.class),
                    defaultFactory.createContainer("unrelated.topic").getCommonErrorHandler());
        }
    }

    @Test
    void productionBackOffIsFiveSecondsWithFiveRetriesAfterInitialAttempt() {
        try (var context = context()) {
            FixedBackOff backOff = context.getBean(FixedBackOff.class);
            assertEquals(5_000L, backOff.getInterval());
            assertEquals(5L, backOff.getMaxAttempts());
            BackOffExecution execution = backOff.start();
            for (int retry = 1; retry <= 5; retry++) {
                assertEquals(5_000L, execution.nextBackOff(), "retry " + retry);
            }
            assertEquals(BackOffExecution.STOP, execution.nextBackOff(), "six total attempts, not six retries");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"material.deleted.v1", "material.details.upserted.v1",
            "material.titles.updated.v1", "user.teacher-role-revoked.v1"})
    @SuppressWarnings("unchecked")
    void resolverAndRecovererPublishToOriginalTopicDltAndOriginalPartition(String topic) {
        try (var context = context()) {
            ConsumerRecord<String, String> record = new ConsumerRecord<>(topic, 3, 42L, "key", "original payload");
            RuntimeException failure = new IllegalStateException("retries exhausted");
            var resolver = (BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition>)
                    context.getBean(RESOLVER, BiFunction.class);
            assertEquals(new TopicPartition(topic + ".DLT", 3), resolver.apply(record, failure));

            KafkaOperations<String, String> operations = context.getBean(KafkaOperations.class);
            when(operations.send(any(ProducerRecord.class))).thenAnswer(invocation -> {
                ProducerRecord<String, String> sent = invocation.getArgument(0);
                return CompletableFuture.completedFuture(new SendResult<>(sent, mock(RecordMetadata.class)));
            });
            DeadLetterPublishingRecoverer recoverer = context.getBean(DeadLetterPublishingRecoverer.class);
            recoverer.accept(record, mock(Consumer.class), failure);

            ArgumentCaptor<ProducerRecord<String, String>> sent = ArgumentCaptor.forClass(ProducerRecord.class);
            verify(operations).send(sent.capture());
            assertAll(
                    () -> assertEquals(topic + ".DLT", sent.getValue().topic()),
                    () -> assertEquals(3, sent.getValue().partition()),
                    () -> assertEquals(record.key(), sent.getValue().key()),
                    () -> assertEquals(record.value(), sent.getValue().value())
            );
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void failedDltPublicationMustNotCountAsSuccessfulRecovery() {
        try (var context = context()) {
            KafkaOperations<String, String> operations = context.getBean(KafkaOperations.class);
            when(operations.send(any(ProducerRecord.class))).thenReturn(
                    CompletableFuture.failedFuture(new IllegalStateException("broker unavailable")));
            var recoverer = context.getBean(DeadLetterPublishingRecoverer.class);
            var record = new ConsumerRecord<>("material.deleted.v1", 3, 42L, "key", "payload");
            assertThrows(RuntimeException.class, () -> recoverer.accept(record, mock(Consumer.class),
                    new InvalidIntegrationEventException("invalid event")));
            verify(operations).send(any(ProducerRecord.class));
        }
    }

    @ParameterizedTest(name = "database failure={0}: recover only on sixth failed delivery")
    @ValueSource(booleans = {false, true})
    void runtimeAndDatabaseFailuresGetFiveRetries(boolean database) throws Exception {
        RuntimeException failure = database ? new DataAccessResourceFailureException("database unavailable")
                : new IllegalStateException("transient application failure");
        assertRecoveryAttempt(failure, 6);
    }

    @ParameterizedTest(name = "listener wrapper={0}: invalid event recovers immediately")
    @ValueSource(booleans = {false, true})
    void invalidEventsAreNonRetryableIncludingListenerWrapper(boolean wrapped) throws Exception {
        Class<? extends RuntimeException> invalidType = requiredType(INVALID_EVENT).asSubclass(RuntimeException.class);
        RuntimeException invalid = invalidType.getConstructor(String.class).newInstance("invalid integration event");
        Exception failure = wrapped ? new ListenerExecutionFailedException("listener failed", invalid) : invalid;
        assertRecoveryAttempt(failure, 1);
    }

    private void assertRecoveryAttempt(Exception failure, int totalAttempts) throws Exception {
        ConsumerRecordRecoverer recoverer = mock(ConsumerRecordRecoverer.class);
        FixedBackOff noWait = spy(new FixedBackOff(0L, 5L));
        Class<?> configurationType = requiredType(CONFIGURATION);
        Object configuration = configurationType.getConstructor().newInstance();
        // Only the configuration's public API is reflected on, never Spring internals.
        Object result = configurationType.getMethod("classroomIntegrationErrorHandler",
                        ConsumerRecordRecoverer.class, FixedBackOff.class)
                .invoke(configuration, recoverer, noWait);
        DefaultErrorHandler handler = assertInstanceOf(DefaultErrorHandler.class, result);
        handler.setSeekAfterError(false);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("material.deleted.v1", 3, 42L, "key", "payload");
        Consumer<?, ?> consumer = mock(Consumer.class);
        MessageListenerContainer container = mock(MessageListenerContainer.class);

        for (int attempt = 1; attempt < totalAttempts; attempt++) {
            assertFalse(handler.handleOne(failure, record, consumer, container), "attempt " + attempt + " must retry");
            verifyNoInteractions(recoverer);
        }
        assertTrue(handler.handleOne(failure, record, consumer, container), "must recover on attempt " + totalAttempts);
        verify(recoverer, times(1)).accept(record, failure);
        verifyNoMoreInteractions(recoverer);
        if (totalAttempts > 1) {
            verify(noWait, atLeastOnce()).start();
        }
    }

    private AnnotationConfigApplicationContext context() {
        Class<?> configurationType = requiredType(CONFIGURATION);
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("testKafkaProperties",
                Map.of("spring.kafka.listener.ack-mode", "record")));
        context.registerBean("consumerFactory", ConsumerFactory.class, () -> mock(ConsumerFactory.class));
        context.registerBean("kafkaTemplate", KafkaTemplate.class, () -> mock(KafkaTemplate.class));
        context.register(configurationType, KafkaAutoConfiguration.class);
        try {
            context.refresh();
            return context;
        } catch (RuntimeException | Error failure) {
            context.close();
            throw failure;
        }
    }

    private static Class<?> requiredType(String name) {
        return assertDoesNotThrow(() -> Class.forName(name), "Missing future production contract type: " + name);
    }
}
