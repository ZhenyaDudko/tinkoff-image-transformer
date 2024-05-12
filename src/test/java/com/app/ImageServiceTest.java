package com.app;

import com.app.exception.ImageNotFoundException;
import com.app.exception.NotSupportedTypeOfImageException;
import com.app.model.user.Role;
import com.app.model.user.User;
import com.app.repository.FilterQueryRepository;
import com.app.repository.ImageMetaRepository;
import com.app.repository.UserRepository;
import com.app.service.ImageService;
import com.app.util.FilesCreator;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.errors.ErrorResponseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImageServiceTest extends AbstractTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageMetaRepository imageMetaRepository;
    @Autowired
    private MinioClient client;
    @Autowired
    private FilterQueryRepository filterQueryRepository;

    @BeforeEach
    public void init() {
        userRepository.save(new User().setUsername("user").setPassword("123").setRole(Role.ROLE_USER));
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAllInBatch();
        imageMetaRepository.deleteAllInBatch();
        client.removeObjects(RemoveObjectsArgs.builder().bucket("minio-storage").build());
    }

    @ValueSource(strings = {"image/jpeg", "image/png"})
    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(username = "user", password = "123")
    public void uploadAndDownloadImage(String fileType) {
        var testFile = FilesCreator.createTestFile(fileType, "test");

        var imageId = imageService.uploadImage(testFile);
        var fileAndType = imageService.downloadImage(imageId);
        assertArrayEquals(fileAndType.getFirst(), testFile.getBytes());
        assertEquals(fileType, fileAndType.getSecond());
    }

    @ValueSource(strings = {"image/jpeg", "image/png"})
    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(username = "user", password = "123")
    public void uploadAndDeleteImage(String fileType) {
        var testFile = FilesCreator.createTestFile(fileType, "test");

        var imageId = imageService.uploadImage(testFile);
        imageService.deleteImage(imageId);

        assertThrows(ErrorResponseException.class, () ->
                client.getObject(
                        GetObjectArgs
                                .builder()
                                .bucket("minio-storage")
                                .object(imageId)
                                .build())
        );

    }

    @ValueSource(strings = {"image/jpg"})
    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(username = "user", password = "123")
    public void incorrectImageForUpload(String fileType) {
        var testFile = FilesCreator.createTestFile(fileType, "test");

        assertThrows(NotSupportedTypeOfImageException.class, () -> imageService.uploadImage(testFile));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "user", password = "123")
    public void imageNotFoundForDownload() {
        assertThrows(ImageNotFoundException.class, () -> imageService.downloadImage("123"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "user", password = "123")
    public void imageNotFoundForDelete() {
        assertThrows(ImageNotFoundException.class, () -> imageService.deleteImage("123"));
    }

    @ValueSource(strings = {"image/jpeg", "image/png"})
    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(username = "user", password = "123")
    public void uploadAndGetImages(String fileType) {
        var testFile1 = FilesCreator.createTestFile(fileType, "test1");
        var testFile2 = FilesCreator.createTestFile(fileType, "test2");

        var imageId1 = imageService.uploadImage(testFile1);
        var imageId2 = imageService.uploadImage(testFile2);

        var images = imageService.getImages();

        assertEquals(2, images.size());
        assertEquals(imageId1, images.get(0).getImageId());
        assertEquals(fileType, images.get(0).getMediaType());
        assertEquals("test1", images.get(0).getName());

        assertEquals(imageId2, images.get(1).getImageId());
        assertEquals(fileType, images.get(1).getMediaType());
        assertEquals("test2", images.get(1).getName());
    }

    @ValueSource(strings = {"image/jpeg", "image/png"})
    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(username = "user", password = "123")
    public void uploadAndFilterImage(String fileType) {
        var testFile1 = FilesCreator.createTestFile(fileType, "test1");

        var imageId1 = imageService.uploadImage(testFile1);

        var requestId = imageService.filterImage(imageId1, List.of(
                ImageService.Filter.BLUR,
                ImageService.Filter.GRAYSCALE)
        );

        var request = filterQueryRepository.findByRequestId(requestId);
        assertFalse(request.isEmpty());
        assertEquals(imageId1, request.get().getImageId());
    }
}
