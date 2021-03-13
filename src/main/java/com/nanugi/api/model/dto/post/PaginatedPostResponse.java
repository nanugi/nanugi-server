package com.nanugi.api.model.dto.post;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedPostResponse {

    private int page;
    private String previous;
    private String next;
    private int size;
    private List<PostResponse> posts;
}
