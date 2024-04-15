package com.app;

import com.app.exception.ImageNotFoundException;
import com.app.exception.NotSupportedTypeOfImageException;
import com.app.model.user.Role;
import com.app.model.user.User;
import com.app.repository.ImageMetaRepository;
import com.app.repository.UserRepository;
import com.app.service.ImageService;
import com.app.util.FilesCreator;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImageServiceTest extends AbstractTest {
    @Autowired
    private FilesCreator filesCreator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageMetaRepository imageMetaRepository;
    @Autowired
    private MinioClient client;

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
        var testFile = filesCreator.createTestFile(fileType, "test");

        var imageId = imageService.uploadImage(testFile);
        var fileAndType = imageService.downloadImage(imageId);
        assertArrayEquals(fileAndType.getFirst(), testFile.getBytes());
        assertEquals(fileType, fileAndType.getSecond());
    }

    @ValueSource(strings = {"image/jpg"})
    @SneakyThrows
    @ParameterizedTest
    @WithMockUser(username = "user", password = "123")
    public void incorrectImageForUpload(String fileType) {
        var testFile = filesCreator.createTestFile(fileType, "test");

        assertThrows(NotSupportedTypeOfImageException.class, () -> imageService.uploadImage(testFile));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "user", password = "123")
    public void imageNotFoundForDownload() {
        assertThrows(ImageNotFoundException.class, () -> imageService.downloadImage("123"));
    }
}
