package com.timcritt.tfg.infrastructure.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.function.BiFunction;

@Configuration(proxyBeanMethods = false)
public class ClassroomIntegrationKafkaConfiguration {

    @Bean
    public FixedBackOff classroomIntegrationBackOff() {
        return new FixedBackOff(5_000L, 5L);
    }

    @Bean
    public BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> classroomIntegrationDltDestinationResolver() {
        return (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition());
    }

    @Bean
    public DeadLetterPublishingRecoverer classroomIntegrationDeadLetterRecoverer(
            KafkaOperations<?, ?> kafkaOperations,
            @Qualifier("classroomIntegrationDltDestinationResolver")
            BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> destinationResolver
    ) {
        var recoverer = new DeadLetterPublishingRecoverer(kafkaOperations, destinationResolver);
        // Never fall back to a producer-selected partition if DLT metadata is unavailable.
        recoverer.setVerifyPartition(false);
        recoverer.setFailIfSendResultIsError(true);
        return recoverer;
    }

    // Explicit qualification keeps this policy out of Boot's default listener factory.
    @Bean(defaultCandidate = false)
    public DefaultErrorHandler classroomIntegrationErrorHandler(
            @Qualifier("classroomIntegrationDeadLetterRecoverer") ConsumerRecordRecoverer recoverer,
            @Qualifier("classroomIntegrationBackOff") FixedBackOff backOff
    ) {
        var handler = new DefaultErrorHandler(recoverer, backOff);
        handler.addNotRetryableExceptions(InvalidIntegrationEventException.class);
        return handler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> classroomIntegrationKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory,
            @Qualifier("classroomIntegrationErrorHandler") DefaultErrorHandler errorHandler
    ) {
        // Boot's configurer uses Object/Object generics; serializers still come from Boot properties.
        var factory = new ConcurrentKafkaListenerContainerFactory<Object, Object>();
        configurer.configure(factory, consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
