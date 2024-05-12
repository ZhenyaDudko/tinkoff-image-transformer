package com.app.util;

import lombok.SneakyThrows;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FilesCreator {
    @SneakyThrows
    public static MultipartFile createTestFile(String fileType, String name) {
        String content = "This is a test file content";
        byte[] contentBytes = content.getBytes();

        return new MockMultipartFile(name, name, fileType, contentBytes);
    }
}
