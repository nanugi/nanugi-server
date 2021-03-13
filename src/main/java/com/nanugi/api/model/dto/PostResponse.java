package com.nanugi.api.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class PostResponse {

    private Long post_id;
    private boolean is_close;
    private MemberResponse user;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    private  PostNanumInfoResponse detail;
}
