package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetImagesResponse {
    private List<ImageResponse> images;
}
