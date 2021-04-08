package com.nanugi.api.model.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PostListResponse {
    Long post_id;
    String title;
    String nickname;
    String thumbnail;
    int totalPrice;
    int minParti;
    int maxParti;
    int liked;
    boolean is_close;
    LocalDateTime createdAt;
}
