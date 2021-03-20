package com.nanugi.api.model.dto.post;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostListResponse {
    Long post_id;
    String title;
    String thumbnail;
    int nanumPrice;
    int minParti;
    int maxParti;
    boolean is_close;
}
