package com.wisestudent.utils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@NoArgsConstructor
public class FilesCreator {
    @SneakyThrows
    public MultipartFile createTestFile(String fileType, String name) {
        String fileName = name + "." + fileType;
        String content = "This is a test file content for " + fileType;
        byte[] contentBytes = content.getBytes();

        return new MockMultipartFile(fileName, fileName, Files.probeContentType(Path.of(fileName)) + fileType, contentBytes);
    }
}
