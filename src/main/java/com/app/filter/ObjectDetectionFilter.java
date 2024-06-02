package com.app.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class ObjectDetectionFilter {

    /**
     * Imagga upload image endpoint.
     */
    private static final String UPLOAD_URL =
      "https://api.imagga.com/v2/uploads";

    /**
     * Imagga get image tags endpoint.
     */
    private static final String TAGS_URL =
      "https://api.imagga.com/v2/tags";

    /**
     * Imagga api key.
     */
    private static final String API_KEY =
            "YWNjX2Y2ZTU5N2JkOGZkNGU4NTphNjBlZmI5"
                    + "NzYwY2U1ZWViNmM5ZjlhZDUxNmEwMGUzZg==";

    private ObjectDetectionFilter() {
    }

    /**
     * Apply filter.
     *
     * @param imageData image.
     * @param mediaType media type.
     * @return result image.
     * @throws IOException
     */
    @CircuitBreaker(name = "CircuitBreaker")
    @Retry(name = "Retry")
    @RateLimiter(name = "RateLimiter")
    public static byte[] applyFilter(
            final byte[] imageData,
            final String mediaType
    ) throws IOException {
        log.info("Applying object detection filter");

        RestClient client = RestClient.create();

        Resource fileResource = new ByteArrayResource(imageData) {
            @Override
            public String getFilename() {
                return "image.jpg";
            }
        };

        // Prepare the body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", fileResource);

        String uploadId = Objects.requireNonNull(client.post()
                .uri(UPLOAD_URL)
                .header("Authorization", "Basic " + API_KEY)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(UploadResult.class))
                .result
                .uploadId;

        List<TagsResult.Result.TagResult> tags =
                Objects.requireNonNull(client.get()
                        .uri(TAGS_URL + "?image_upload_id=" + uploadId)
                        .header("Authorization", "Basic " + API_KEY)
                        .retrieve()
                        .body(TagsResult.class))
                        .result.tags;

        List<String> topTagNames = tags.stream()
                .sorted((t1, t2) ->
                        Double.compare(t2.confidence, t1.confidence)
                )
                .limit(3)
                .map(TagsResult.Result.TagResult::getTag)
                .map(TagsResult.Result.TagResult.Tag::getEn)
                .toList();

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        Font font = new Font("Arial", Font.BOLD, 18);
        Graphics g = image.getGraphics();
        g.setFont(font);
        g.setColor(Color.RED);
        for (int i = 0; i < topTagNames.size(); i++) {
            g.drawString(topTagNames.get(i), 10, 30 * i + 20);
        }
        g.dispose();

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String imageFormat = mediaType.contains("png") ? "png" : "jpeg";
        ImageIO.write(image, imageFormat, output);
        return output.toByteArray();
    }

    @Data
    public static class UploadResult {

        /** Upload result. */
        @JsonProperty("result")
        private Result result;

        @Data
        public static class Result {

            /** Uploaded imgae id. */
            @JsonProperty("upload_id")
            private String uploadId;
        }
    }

    @Data
    public static class TagsResult {

        /** Upload result. */
        @JsonProperty("result")
        private Result result;

        @Data
        public static class Result {

            /** Image tags results. */
            @JsonProperty("tags")
            private List<TagResult> tags;

            @Data
            public static class TagResult {

                /** Tag confidence. */
                @JsonProperty("confidence")
                private double confidence;

                /** Tag. */
                @JsonProperty("tag")
                private Tag tag;

                @Data
                public static class Tag {

                    /** Tag name. */
                    @JsonProperty("en")
                    private String en;
                }
            }
        }
    }
}
