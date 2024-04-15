package com.app.service;

import com.app.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
