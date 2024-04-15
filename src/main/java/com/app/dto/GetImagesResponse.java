package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class GetImagesResponse {
    /**
     * All images uploaded by user.
     */
    private List<ImageResponse> images;
}
