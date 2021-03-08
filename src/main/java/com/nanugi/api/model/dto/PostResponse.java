package com.nanugi.api.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PostResponse {

    private Long post_id;
    private MemberResponse user;
    private String title;
    private String content;
    private int price;

    private int minParti;
    private int maxParti;
    private int nanumPrice;
    private String chatUrl;

    private LocalDateTime createdAt;
}
