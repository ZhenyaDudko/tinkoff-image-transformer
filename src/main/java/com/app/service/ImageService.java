package com.app.service;

import com.app.exception.FilterRequestNotFoundException;
import com.app.exception.ImageIsTooBigException;
import com.app.exception.ImageNotAccessibleException;
import com.app.exception.ImageNotFoundException;
import com.app.exception.NotSupportedTypeOfImageException;
import com.app.kafka.ImageWipMessage;
import com.app.kafka.KafkaSender;
import com.app.model.FilterQuery;
import com.app.model.ImageMeta;
import com.app.model.user.User;
import com.app.repository.FilterQueryRepository;
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
import java.util.UUID;

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
    private final ImageMetaRepository imageMetaRepository;

    /**
     * Filter query repository.
     */
    private final FilterQueryRepository filterQueryRepository;

    /**
     * Image storage service.
     */
    private final MinioService imageStorage;

    /**
     * User service.
     */
    private final UserService userService;

    /**
     * Kafka sender service.
     */
    private final KafkaSender kafkaSender;

    /**
     * Delete image.
     * @param imageId
     * @throws Exception
     */
    public void deleteImage(final String imageId) throws Exception {
        ImageMeta imageMeta = getAndCheckImage(imageId);

        imageStorage.deleteImage(imageId);
        imageMetaRepository.deleteById(imageMeta.getId());
    }

    /**
     * Download image.
     * @param imageId
     * @return a pair of image in bytes and its type.
     * @throws Exception
     */
    public Pair<byte[], String> downloadImage(final String imageId)
            throws Exception {
        ImageMeta imageMeta = getAndCheckImage(imageId);

        return Pair.of(
                imageStorage.downloadImage(imageId),
                imageMeta.getMediaType()
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
        imageMetaRepository.save(new ImageMeta()
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
        return imageMetaRepository.findAllByUserId(user.getId());
    }

    /**
     * Handle filter image query.
     * @param imageId
     * @param filters
     * @return an id of query in system.
     */
    public String filterImage(String imageId, List<Filter> filters) throws Exception {
        ImageMeta imageMeta = getAndCheckImage(imageId);
        String requestId = UUID.randomUUID().toString();
        filterQueryRepository.save(new FilterQuery()
                .setImageId(imageId)
                .setStatus(FilterQuery.Status.WIP)
                .setRequestId(requestId)
        );
        kafkaSender.sendMessage(new ImageWipMessage(imageId, requestId, filters.stream().map(Enum::toString).toList()));

        return requestId;
    }

    public Pair<String, FilterQuery.Status> getFilterImageStatus(String imageId, String requestId) throws Exception {
        ImageMeta imageMeta = getAndCheckImage(imageId);
        Optional<FilterQuery> optionalFilterQuery = filterQueryRepository.findByRequestId(requestId);
        if (optionalFilterQuery.isEmpty()) {
            throw new FilterRequestNotFoundException();
        }
        FilterQuery filterQuery = optionalFilterQuery.get();

        if (filterQuery.getStatus() == FilterQuery.Status.WIP) {
            return Pair.of(filterQuery.getImageId(), FilterQuery.Status.WIP);
        } else {
            return Pair.of(filterQuery.getFilteredImageId(), FilterQuery.Status.DONE);
        }
    }

    private ImageMeta getAndCheckImage(String imageId) throws ImageNotFoundException, ImageNotAccessibleException {
        Optional<ImageMeta> imageMeta =
                imageMetaRepository.findImageMetaByImageId(imageId);
        if (imageMeta.isEmpty()) {
            throw new ImageNotFoundException();
        }

        User user = userService.getCurrentUser();
        if (!Objects.equals(imageMeta.get().getUserId(), user.getId())) {
            throw new ImageNotAccessibleException();
        }

        return imageMeta.get();
    }

    public enum Filter {
        REVERS_COLORS,
        CROP,
        REMOVE_BACKGROUND,
        OTHER
    }
}
