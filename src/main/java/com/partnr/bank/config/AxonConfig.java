package com.partnr.bank.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public EntityManagerProvider entityManagerProvider(EntityManagerFactory emf) {
        org.axonframework.springboot.util.jpa.ContainerManagedEntityManagerProvider provider =
                new org.axonframework.springboot.util.jpa.ContainerManagedEntityManagerProvider();
        provider.setEntityManager(emf.createEntityManager());
        return provider;
    }

    @Bean
    public JpaEventStorageEngine eventStorageEngine(Serializer serializer,
                                                     EntityManagerProvider entityManagerProvider,
                                                     TransactionManager transactionManager) {
        return JpaEventStorageEngine.builder()
                .eventSerializer(serializer)
                .snapshotSerializer(serializer)
                .entityManagerProvider(entityManagerProvider)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public EventStore eventStore(JpaEventStorageEngine eventStorageEngine) {
        return EmbeddedEventStore.builder()
                .storageEngine(eventStorageEngine)
                .build();
    }

    @Bean
    public SnapshotTriggerDefinition bankAccountSnapshotTrigger(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }
}
