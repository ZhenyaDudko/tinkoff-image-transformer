package com.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * Minio server url.
     */
    private String url;

    /**
     * Minio server port.
     */
    private int port;

    /**
     * Minio server access key.
     */
    private String accessKey;

    /**
     * Minio server secret key.
     */
    private String secretKey;

    /**
     * Is connection to Minio server secure.
     */
    private boolean secure;

    /**
     * Default bucket name.
     */
    private String bucket;

    /**
     * Maximum image size for upload.
     */
    private long imageSize;
}
