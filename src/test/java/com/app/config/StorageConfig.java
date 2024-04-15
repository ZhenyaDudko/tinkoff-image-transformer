package com.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.time.Duration;
import java.util.List;

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
                                .withLogConsumer(new Slf4jLogConsumer(log));
                minioContainer.setPortBindings(List.of("9000:9000", "9001:9001"));
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
                    "minio.port=" + minioContainer.getExposedPorts().get(0),
                    "minio.accessKey=" + minioContainer.getUserName(),
                    "minio.secretKey=" + minioContainer.getPassword(),
                    "minio.secure=false",
                    "minio.bucket=minio-storage"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
