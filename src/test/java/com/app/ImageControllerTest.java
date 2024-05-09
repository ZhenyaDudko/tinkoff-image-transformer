package com.app;

import com.app.dto.GetImagesResponse;
import com.app.dto.UiSuccessContainer;
import com.app.model.user.Role;
import com.app.model.user.User;
import com.app.repository.ImageMetaRepository;
import com.app.repository.UserRepository;
import com.app.util.FilesCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerTest extends AbstractTest {

    @Autowired
    private FilesCreator filesCreator;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageMetaRepository imageMetaRepository;
    @Autowired
    private MinioClient client;

    private final ObjectMapper mapper = new ObjectMapper();

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

    @Test
    @WithMockUser(username = "user", password = "123")
    void imageNotFoundForDownload() throws Exception {
        var expectedResponse = new UiSuccessContainer(false, "Image not found");

        this.mockMvc.perform(get("/api/v1/image/123")).andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));
    }

    @Test
    @WithMockUser(username = "user", password = "123")
    void getImages() throws Exception {
        var expectedResponse = new GetImagesResponse(List.of());

        this.mockMvc.perform(get("/api/v1/images")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResponse)));
    }

}
