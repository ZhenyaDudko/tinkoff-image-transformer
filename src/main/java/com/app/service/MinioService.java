package com.app.service;

import com.app.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketLifecycleArgs;
import io.minio.messages.Expiration;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.LifecycleRule;
import io.minio.messages.RuleFilter;
import io.minio.messages.Status;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class MinioService {

    /**
     * Minio client.
     */
    private final MinioClient client;

    /**
     * Minio properties.
     */
    private final MinioProperties properties;

    /**
     * TTL for intermediate files.
     */
    @Value("${spring.kafka.ttl:1}")
    private int ttl;

    /**
     * Create bucket and set expiration policy for intermediate images.
     * @throws Exception
     */
    @PostConstruct
    public void init() throws Exception {
        boolean bucketExists = client.bucketExists(BucketExistsArgs
                .builder()
                .bucket(properties.getBucket())
                .build()
        );
        if (!bucketExists) {
            client.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket(properties.getBucket())
                    .build()
            );
        }
        List<LifecycleRule> rules = new ArrayList<>();
        rules.add(
                new LifecycleRule(
                        Status.ENABLED,
                        null,
                        new Expiration((ZonedDateTime) null, ttl, null),
                        new RuleFilter("intermediate/"),
                        "rule1",
                        null,
                        null,
                        null));
        LifecycleConfiguration config = new LifecycleConfiguration(rules);
        client.setBucketLifecycle(SetBucketLifecycleArgs
                .builder()
                .bucket(properties.getBucket())
                .config(config)
                .build());
    }

    /**
     * Upload image.
     * @param file
     * @return image id.
     * @throws Exception
     */
    public String uploadImage(final MultipartFile file) throws Exception {
        String imageId = UUID.randomUUID().toString();

        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        checkBucket();
        client.putObject(
                PutObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(imageId)
                        .stream(
                                inputStream,
                                file.getSize(),
                                properties.getImageSize()
                        )
                        .contentType(file.getContentType())
                        .build()
        );

        return imageId;
    }

    /**
     * Upload intermediate image.
     * @param file
     * @param mediaType
     * @param prefix
     * @return image id.
     * @throws Exception
     */
    public String uploadImage(
            final byte[] file,
            final String mediaType,
            final String prefix
    ) throws Exception {
        String imageId = prefix + UUID.randomUUID();

        InputStream inputStream = new ByteArrayInputStream(file);
        checkBucket();
        client.putObject(
                PutObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(imageId)
                        .stream(
                                inputStream,
                                file.length,
                                properties.getImageSize()
                        )
                        .contentType(mediaType)
                        .build()
        );

        return imageId;
    }

    /**
     * Upload image.
     * @param file
     * @param mediaType
     * @return image id.
     * @throws Exception
     */
    public String uploadImage(
            final byte[] file,
            final String mediaType
    ) throws Exception {
        return uploadImage(file, mediaType, "");
    }

    /**
     * Download image.
     * @param imageId
     * @return image in bytes.
     * @throws Exception
     */
    public byte[] downloadImage(final String imageId) throws Exception {
        checkBucket();
        return IOUtils.toByteArray(client.getObject(
                GetObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(imageId)
                        .build()));
    }

    /**
     * Delete image.
     * @param imageId
     * @throws Exception
     */
    public void deleteImage(final String imageId) throws Exception {
        checkBucket();
        client.removeObject(RemoveObjectArgs.builder()
                .bucket(properties.getBucket())
                .object(imageId)
                .build()
        );
    }

    /**
     * Check if bucket exists and create otherwise.
     * @throws Exception
     */
    private void checkBucket() throws Exception {
        if (client.bucketExists(BucketExistsArgs
                .builder()
                .bucket(properties.getBucket())
                .build())
        ) {
            return;
        }
        client.makeBucket(MakeBucketArgs
                .builder()
                .bucket(properties.getBucket())
                .build()
        );
    }
}
