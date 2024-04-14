package com.wisestudent.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static java.util.Objects.isNull;

@Slf4j
public class StorageConfig {

    private static volatile MinIOContainer minioContainer = null;


    private static MinIOContainer getMinioContainer() {
        MinIOContainer instance = minioContainer;
        if (isNull(minioContainer)) {
            synchronized (MinIOContainer.class) {
                minioContainer = instance =
                        new MinIOContainer("minio/minio:latest")
                                .withReuse(true)
                                .withLogConsumer(new Slf4jLogConsumer(log))
                                .withExposedPorts(9000)
                                .withUserName("minioadmin")
                                .withPassword("minioadmin")
                                .withStartupTimeout(Duration.ofSeconds(60));
                minioContainer.start();
            }
        }
        return instance;
    }

    @Component("MinioInitializer")
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            var minioContainer = getMinioContainer();
            TestPropertyValues.of(
                    "minio.url=" + minioContainer.getS3URL(),
                    "minio.access-key=" + minioContainer.getUserName(),
                    "minio.secret-key=" + minioContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
