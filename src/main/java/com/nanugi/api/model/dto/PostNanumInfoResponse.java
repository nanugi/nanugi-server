package com.nanugi.api.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostNanumInfoResponse {
    private int price;
    private int nanumPrice;
    private int minParti;
    private int maxParti;
    private String chatUrl;
}
