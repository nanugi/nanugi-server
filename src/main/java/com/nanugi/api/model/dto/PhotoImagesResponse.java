package com.nanugi.api.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PhotoImagesResponse {
    private int count;
    private List<ImageResponse> images;
}
