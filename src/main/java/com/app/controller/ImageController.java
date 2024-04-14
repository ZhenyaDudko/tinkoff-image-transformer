package com.app.controller;

import com.app.dto.ImageDto;
import com.app.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService service;

    @PostMapping("/load")
    public ImageDto loadImage(MultipartFile file) throws Exception {
        return service.uploadImage(file);
    }

    @GetMapping(value = "/{link}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@PathVariable String link) throws Exception {
        return service.downloadImage(link);
    }

    @GetMapping("/{id}/meta")
    public ImageDto getMeta(@PathVariable int id) {
        return service.getImageMeta(id);
    }
}
