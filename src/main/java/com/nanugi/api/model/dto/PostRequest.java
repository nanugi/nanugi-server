package com.nanugi.api.model.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class PostRequest {
    @NotNull
    @NotEmpty
    @Length(max = 50)
    private String title;
    @NotNull @NotEmpty @Length(max = 800)
    private String content;
    @NotNull
    private int totalPrice;
    @NotNull
    private int nanumPrice;
    @NotNull @Max(value = 10)
    private int minParti;
    @Max(value = 15)
    private int maxParti;
    @NotNull @NotEmpty @Pattern(regexp = "(http|https)://[a-zA-Z0-9./-]*", message = "http, https로 시작하는 유효한 url이어야 합니다.")
    private String chatUrl;
}
