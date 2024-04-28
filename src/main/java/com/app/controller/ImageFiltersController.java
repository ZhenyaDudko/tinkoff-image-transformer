package com.app.controller;

import com.app.dto.ApplyImageFiltersResponse;
import com.app.dto.GetModifiedImageByRequestIdResponse;
import com.app.dto.UploadImageResponse;
import com.app.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Image Filters Controller", description =
        "Применение указанных фильтров к изображению")
public final class ImageFiltersController {

    /**
     * Image managing service.
     */
    private final ImageService service;

    /**
     * Apply filters.
     * @param imageId image id
     * @param filters filters
     * @return request id
     * @throws Exception
     */
    @PostMapping("/image/{image-id}/filters/apply")
    @Operation(
            summary = "Применение указанных фильтров к изображению",
            operationId = "applyImageFilters"
    )
    public ApplyImageFiltersResponse applyFilters(
            @PathVariable final String imageId,
            @RequestParam final List<ImageService.Filter> filters
    ) throws Exception {
        String requestId = service.filterImage(imageId, filters);
        return new ApplyImageFiltersResponse(requestId);
    }

    /**
     * Get filtered image.
     * @param imageId image id
     * @return image.
     * @throws Exception
     */
    @GetMapping("/image/{image-id}/filters/{request-id}")
    @Operation(
            summary = "Получение ИД измененного файла по ИД запроса",
            operationId = "getModifiedImageByRequestId"
    )
    public GetModifiedImageByRequestIdResponse getFilteredImage(
            @PathVariable final String imageId,
            @PathVariable final String requestId
    ) throws Exception {
        var result = service.getFilterImageStatus(imageId, requestId);
        return new GetModifiedImageByRequestIdResponse(result.getFirst(), result.getSecond().toString());
    }
}
