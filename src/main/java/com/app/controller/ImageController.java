package com.app.controller;

import com.app.dto.GetImagesResponse;
import com.app.dto.ImageResponse;
import com.app.dto.UiSuccessContainer;
import com.app.dto.UploadImageResponse;
import com.app.model.ImageMeta;
import com.app.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Image Controller", description = "Базовый CRUD API для работы с картинками")
public class ImageController {

    private final ImageService service;

    @PostMapping("/image")
    @Operation(summary = "Загрузка нового изображения в систему", operationId = "uploadImage")
    public UploadImageResponse loadImage(MultipartFile file) throws Exception {
        String imageId = service.uploadImage(file);
        return new UploadImageResponse(imageId);
    }

    @GetMapping("/image/{imageId}")
    @Operation(summary = "Скачивание файла по ИД", operationId = "downloadImage")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) throws Exception {
        Pair<byte[], String> res = service.downloadImage(imageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(res.getSecond()));
        return new ResponseEntity<>(res.getFirst(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/image/{imageId}")
    @Operation(summary = "Удаление файла по ИД", operationId = "deleteImage")
    public UiSuccessContainer deleteImage(@PathVariable String imageId) throws Exception {
        service.deleteImage(imageId);
        return new UiSuccessContainer(true, null);
    }

    @GetMapping(value = "/images")
    @Operation(summary = "Получение списка изображений, которые доступны пользователю", operationId = "getImages")
    public GetImagesResponse getImages() {
        List<ImageMeta> imagesMeta = service.getImages();
        List<ImageResponse> imageResponses = imagesMeta
                .stream()
                .map(imageMeta ->
                        new ImageResponse(imageMeta.getImageId(), imageMeta.getName(), imageMeta.getSize()))
                .toList();
        return new GetImagesResponse(imageResponses);
    }
}
