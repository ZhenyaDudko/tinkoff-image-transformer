package com.app.service;

import com.app.exception.ImageIsTooBigException;
import com.app.exception.ImageNotAccessibleException;
import com.app.exception.ImageNotFoundException;
import com.app.exception.NotSupportedTypeOfImageException;
import com.app.model.ImageMeta;
import com.app.model.user.User;
import com.app.repository.ImageMetaRepository;
import com.app.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class ImageService {

    /**
     * Max allowed size for image to be uploaded.
     */
    private static final long MAX_ALLOWED_IMAGE_SIZE = 10000000;

    /**
     * Allowed image types for uploading.
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE
    );

    /**
     * Image meta repository.
     */
    private final ImageMetaRepository repository;

    /**
     * Image storage service.
     */
    private final MinioService imageStorage;

    /**
     * User service.
     */
    private final UserService userService;

    /**
     * Delete image.
     * @param imageId
     * @throws Exception
     */
    public void deleteImage(final String imageId) throws Exception {
        Optional<ImageMeta> imageMeta =
                repository.findImageMetaByImageId(imageId);
        if (imageMeta.isEmpty()) {
            throw new ImageNotFoundException();
        }

        User user = userService.getCurrentUser();
        if (!Objects.equals(imageMeta.get().getUserId(), user.getId())) {
            throw new ImageNotAccessibleException();
        }

        imageStorage.deleteImage(imageId);
        repository.deleteById(imageMeta.get().getId());
    }

    /**
     * Download image.
     * @param imageId
     * @return a pair of image in bytes and its type.
     * @throws Exception
     */
    public Pair<byte[], String> downloadImage(final String imageId)
            throws Exception {
        Optional<ImageMeta> imageMeta =
                repository.findImageMetaByImageId(imageId);
        if (imageMeta.isEmpty()) {
            throw new ImageNotFoundException();
        }

        User user = userService.getCurrentUser();
        if (!Objects.equals(imageMeta.get().getUserId(), user.getId())) {
            throw new ImageNotAccessibleException();
        }

        return Pair.of(
                imageStorage.downloadImage(imageId),
                imageMeta.get().getMediaType()
        );
    }

    /**
     * Upload image.
     * @param file
     * @return image id.
     * @throws Exception
     */
    public String uploadImage(final MultipartFile file) throws Exception {
        if (file.getSize() > MAX_ALLOWED_IMAGE_SIZE) {
            throw new ImageIsTooBigException(
                    file.getSize(),
                    MAX_ALLOWED_IMAGE_SIZE
            );
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new NotSupportedTypeOfImageException();
        }

        User user = userService.getCurrentUser();

        String imageId = imageStorage.uploadImage(file);
        repository.save(new ImageMeta()
                .setImageId(imageId)
                .setName(file.getOriginalFilename())
                .setSize(file.getSize())
                .setMediaType(file.getContentType())
                .setUserId(user.getId())
        );

        return imageId;
    }

    /**
     * Get image meta for all images uploaded by user.
     * @return list of image meta.
     */
    public List<ImageMeta> getImages() {
        User user = userService.getCurrentUser();
        return repository.findAllByUserId(user.getId());
    }
}
