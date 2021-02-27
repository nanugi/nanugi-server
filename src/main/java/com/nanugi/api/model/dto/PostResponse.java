package com.nanugi.api.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@RequiredArgsConstructor
public class PostResponse {

    private Long board_id;
    private UserResponse user;
    private String title;
    private String content;
    private int price;

    private int minParti;
    @Nullable
    private int maxParti;
    private int nanumPrice;
    private String chatUrl;

    public PostResponse(Long id, UserResponse user, String title, String content, int price, int nanumPrice, String chatUrl, int minParti, @Nullable int maxParti){
        this.board_id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.price = price;
        this.minParti = minParti;
        this.maxParti = maxParti;
        this.price = price;
        this.chatUrl = chatUrl;
        this.nanumPrice = nanumPrice;
    }
}
