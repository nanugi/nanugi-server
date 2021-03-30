package com.nanugi.api.model.dto.post;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostNanumInfoResponse {
    private int totalPrice;
    private int minParti;
    private int maxParti;
    private String chatUrl;
}
