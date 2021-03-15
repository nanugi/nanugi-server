package com.nanugi.api.model.dto.image;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PostImageResponse {
    private int count;
    private List<ImageResponse> images;
}
