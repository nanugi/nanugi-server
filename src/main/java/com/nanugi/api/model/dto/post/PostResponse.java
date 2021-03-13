package com.nanugi.api.model.dto.post;

import com.nanugi.api.model.dto.MemberResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PostResponse {

    private Long post_id;
    private boolean is_close;
    private MemberResponse user;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    private PostNanumInfoResponse detail;
}
