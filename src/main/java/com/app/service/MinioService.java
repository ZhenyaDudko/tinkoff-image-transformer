package com.app.service;

import com.app.config.MinioProperties;
import io.minio.GetObjectArgs;
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
public class MinioService {

    private final MinioClient client;
    private final MinioProperties properties;

    public String uploadImage(MultipartFile file) throws Exception {
        String imageId = UUID.randomUUID().toString();

        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        client.putObject(
                PutObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(imageId)
                        .stream(inputStream, file.getSize(), properties.getImageSize())
                        .contentType(file.getContentType())
                        .build()
        );

        return imageId;
    }

    public byte[] downloadImage(String imageId) throws Exception {
        return IOUtils.toByteArray(client.getObject(
                GetObjectArgs.builder()
                        .bucket(properties.getBucket())
                        .object(imageId)
                        .build()));
    }

    public void deleteImage(String imageId) throws Exception {
        client.removeObject(RemoveObjectArgs.builder()
                .bucket(properties.getBucket())
                .object(imageId)
                .build()
        );
    }
}
