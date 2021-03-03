package com.nanugi.api.model.dto;

import jdk.vm.ci.meta.Local;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
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

    public PostResponse(Long id, UserResponse user, String title, String content, int price, int nanumPrice, String chatUrl, int minParti, int maxParti, LocalDateTime localDateTime){
        this.post_id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.price = price;
        this.minParti = minParti;
        this.maxParti = maxParti;
        this.price = price;
        this.chatUrl = chatUrl;
        this.nanumPrice = nanumPrice;
        this.createdAt = localDateTime;
    }
}
