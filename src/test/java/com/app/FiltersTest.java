package com.app;

import com.app.filter.BlurFilter;
import com.app.filter.EdgeDetectionFilter;
import com.app.filter.GrayscaleFilter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

public class FiltersTest {

    @SneakyThrows
    @Test
    public void blurImage() {
        File imageFile = new File("src/test/resources/image/flower.jpeg");
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        BlurFilter.applyFilter(imageBytes, "jpeg");
    }

    @SneakyThrows
    @Test
    public void grayImage() {
        File imageFile = new File("src/test/resources/image/flower.jpeg");
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        GrayscaleFilter.applyFilter(imageBytes, "jpeg");
    }

    @SneakyThrows
    @Test
    public void edgeImage() {
        File imageFile = new File("src/test/resources/image/flower.jpeg");
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

        EdgeDetectionFilter.applyFilter(imageBytes, "jpeg");
    }

}
