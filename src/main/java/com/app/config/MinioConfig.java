package com.app.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    protected MinioConfig() {
    }

    /**
     * @param properties
     * @return MinioClient bean.
     */
    @Bean
    public static MinioClient minioClient(final MinioProperties properties) {
        return MinioClient.builder()
                .credentials(
                        properties.getAccessKey(),
                        properties.getSecretKey())
                .endpoint(
                        properties.getUrl(),
                        properties.getPort(),
                        properties.isSecure())
                .build();
    }
}
