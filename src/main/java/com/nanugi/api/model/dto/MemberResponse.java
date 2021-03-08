package com.nanugi.api.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponse {
    private String name;
    private String uid;
}