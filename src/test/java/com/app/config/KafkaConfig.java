package com.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
public class KafkaConfig {

    private static volatile KafkaContainer kafkaContainer = null;

    private static KafkaContainer getKafkaContainer() {
        KafkaContainer instance = kafkaContainer;
        if (isNull(kafkaContainer)) {
            synchronized (KafkaContainer.class) {
                kafkaContainer = instance =
                        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
                                .withLogConsumer(new Slf4jLogConsumer(log));
                kafkaContainer.setPortBindings(List.of("9092:9093", "29092:29093"));
                kafkaContainer.start();
            }
        }
        return instance;
    }

    @Component("KafkaInitializer")
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            var kafkaContainer = getKafkaContainer();
            TestPropertyValues.of(
                    "kafka.listener.ack-mode=manual",
                    "kafka.cloud.zookeeper.config.enabled=false",
                    "kafka.cloud.zookeeper.connect-string=localhost:2181"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
