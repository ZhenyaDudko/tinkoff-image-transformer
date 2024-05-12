package com.app;

import com.app.dto.auth.SignUpRequest;
import com.app.repository.ImageMetaRepository;
import com.app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageMetaRepository imageMetaRepository;
    @Autowired
    private MinioClient client;

    private final ObjectMapper mapper = new ObjectMapper();

    @AfterEach
    public void clear() {
        userRepository.deleteAllInBatch();
        imageMetaRepository.deleteAllInBatch();
        client.removeObjects(RemoveObjectsArgs.builder().bucket("minio-storage").build());
    }

    @Test
    void signUp() throws Exception {
        SignUpRequest request = new SignUpRequest("user", "123");

        this.mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

}
