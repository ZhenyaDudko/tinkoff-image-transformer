package com.app.util;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.com.google.common.net.MediaType;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@NoArgsConstructor
public class FilesCreator {
    @SneakyThrows
    public MultipartFile createTestFile(String fileType, String name) {
        String content = "This is a test file content";
        byte[] contentBytes = content.getBytes();

        return new MockMultipartFile(name, name, fileType, contentBytes);
    }
}
