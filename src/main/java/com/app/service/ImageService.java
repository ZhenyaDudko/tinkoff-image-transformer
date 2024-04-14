package com.app.service;

import com.app.dto.UploadImageResponse;
import com.app.exception.ImageIsTooBigException;
import com.app.exception.ImageNotFoundException;
import com.app.exception.NotSupportedTypeOfImageException;
import com.app.mapper.ImagesMapper;
import com.app.model.ImageMeta;
import com.app.repository.ImageMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RedisHash
@RequiredArgsConstructor
public class ImageService {

    private static final long MAX_ALLOWED_IMAGE_SIZE = 10000000;
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE
    );

    private final ImageMetaRepository repository;

    private final MinioService imageStorage;

    public void deleteImage(String imageId) throws Exception {
        Optional<ImageMeta> imageMeta = repository.findImageMetaByImageId(imageId);
        if (imageMeta.isEmpty()) {
            throw new ImageNotFoundException();
        }
        imageStorage.deleteImage(imageId);
        repository.deleteById(imageMeta.get().getId());
    }

    public Pair<byte[], String> downloadImage(String imageId) throws Exception {
        Optional<ImageMeta> imageMeta = repository.findImageMetaByImageId(imageId);
        if (imageMeta.isEmpty()) {
            throw new ImageNotFoundException();
        }

        return Pair.of(imageStorage.downloadImage(imageId), imageMeta.get().getMediaType());
    }

    public UploadImageResponse uploadImage(MultipartFile file) throws Exception {
        if (file.getSize() > MAX_ALLOWED_IMAGE_SIZE) {
            throw new ImageIsTooBigException(file.getSize(), MAX_ALLOWED_IMAGE_SIZE);
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new NotSupportedTypeOfImageException();
        }

        String imageId = imageStorage.uploadImage(file);
        repository.save(new ImageMeta()
                .setImageId(imageId)
                .setName(file.getOriginalFilename())
                .setSize(file.getSize())
                .setMediaType(file.getContentType())
        );

        return new UploadImageResponse(imageId);
    }
}
