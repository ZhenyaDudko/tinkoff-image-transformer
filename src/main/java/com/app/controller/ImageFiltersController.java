package com.app.controller;

import com.app.dto.ApplyImageFiltersResponse;
import com.app.dto.GetModifiedImageByRequestIdResponse;
import com.app.service.ImageService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Image Filters Controller", description =
        "Применение указанных фильтров к изображению")
public class ImageFiltersController {

    /**
     * Image managing service.
     */
    private final ImageService service;

    public ImageFiltersController(ImageService service, MeterRegistry meterRegistry) {
        this.service = service;
        timer = meterRegistry.timer("apply-filter-timer");
        failureCounter = meterRegistry.counter("apply-filter-failure-counter");
    }

    private final Timer timer;

    private final Counter failureCounter;

    /**
     * Apply filters.
     * @param imageId image id
     * @param filters filters
     * @return request id
     * @throws Exception
     */
    @RateLimiter(name = "ApiRateLimiter")
    @PostMapping("/image/{imageId}/filters/apply")
    @Operation(
            summary = "Применение указанных фильтров к изображению",
            operationId = "applyImageFilters"
    )
    public ApplyImageFiltersResponse applyFilters(
            @PathVariable final String imageId,
            @RequestParam final List<ImageService.Filter> filters
    ) throws Exception {
        Timer.Sample sample = Timer.start();
        try {
            String requestId = service.filterImage(imageId, filters);
            return new ApplyImageFiltersResponse(requestId);
        } catch (Exception e) {
            failureCounter.increment();
            throw e;
        } finally {
            sample.stop(timer);
        }
    }

    /**
     * Get filtered image.
     * @param imageId image id
     * @param requestId request id
     * @return image.
     * @throws Exception
     */
    @GetMapping("/image/{imageId}/filters/{requestId}")
    @Operation(
            summary = "Получение ИД измененного файла по ИД запроса",
            operationId = "getModifiedImageByRequestId"
    )
    public GetModifiedImageByRequestIdResponse getFilteredImage(
            @PathVariable final String imageId,
            @PathVariable final String requestId
    ) throws Exception {
        var result = service.getFilterImageStatus(imageId, requestId);
        return new GetModifiedImageByRequestIdResponse(
                result.getFirst(),
                result.getSecond().toString()
        );
    }
}
