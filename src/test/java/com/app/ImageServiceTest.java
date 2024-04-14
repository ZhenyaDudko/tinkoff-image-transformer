package com.wisestudent;

import com.wisestudent.dto.FileDto;
import com.wisestudent.exceptions.ImageUploadException;
import com.wisestudent.models.posts.PostFileEntity;
import com.wisestudent.services.FileService;
import com.wisestudent.utils.FilesCreator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;



public class FileServiceTest extends AbstractTest{
    @Autowired
    private FilesCreator filesCreator;
    @Autowired
    private FileService fileService;

    @ValueSource(strings = {"jpg", "pdf", ""})
    @ParameterizedTest
    public void saveFilesAndGetFilesData(String fileType) throws IOException {
        var flag = fileType.isEmpty();
        // Given
        var testFile = (flag)? null : filesCreator.createTestFile(fileType, "test");

        // When
        List<PostFileEntity> savedFiles = null;
        ImageUploadException ex = null;
        if(flag){
            var files = new ArrayList<MultipartFile>();
            files.add(testFile);
            ex = assertThrows(ImageUploadException.class, ()->fileService.saveFiles(files, PostFileEntity.class));
        }else {
            savedFiles = fileService.saveFiles(List.of(testFile), PostFileEntity.class);
        }
        // Then
        if (flag) {
            assertEquals("Image must have name.", ex.getMessage());
        }else {
            assertEquals(savedFiles.size(), 1);
        }

        if(!flag) {
            // When
            List<FileDto> filesData = fileService.getFilesData(new HashSet<>(savedFiles));
            // Then
            assertEquals(filesData.size(), 1);
            assertArrayEquals(filesData.get(0).getFile(), testFile.getBytes());
        }
    }

}
