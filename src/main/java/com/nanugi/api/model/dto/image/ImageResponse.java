package com.nanugi.api.model.dto.image;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ImageResponse {
    private Long id;
    private String url;
    private String caption;
}
