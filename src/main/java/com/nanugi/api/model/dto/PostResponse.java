package com.nanugi.api.model.dto;

import jdk.vm.ci.meta.Local;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Builder
@Data
public class PostResponse {

    private Long post_id;
    private UserResponse user;
    private String title;
    private String content;
    private int price;

    private int minParti;
    private int maxParti;
    private int nanumPrice;
    private String chatUrl;

    private LocalDateTime createdAt;
}
