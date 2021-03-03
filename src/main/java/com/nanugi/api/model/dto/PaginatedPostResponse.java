package com.nanugi.api.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PaginatedPostResponse {

    private int page;
    @Nullable
    private String previous;
    @Nullable
    private String next;
    private int size;
    private List<PostResponse> posts = new ArrayList<>();
}
