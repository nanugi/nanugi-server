package com.nanugi.api.model.dto.post;

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
    @NotNull(message = "제목을 입력하세요")
    @NotEmpty(message = "제목을 입력하세요")
    @Length(max = 50, message = "제목은 50자 이내로 작성해야 합니다")
    private String title;

    @NotNull(message = "내용을 입력하세요")
    @NotEmpty(message = "내용을 입력하세요")
    @Length(min = 10, message = "내용은 10자 이상 작성해야 합니다")
    @Length(max = 800, message = "내용은 800자 이내로 작성해야 합니다")
    private String content;
    private int totalPrice;
    private int nanumPrice;
    @NotNull @Max(value = 10)
    private int minParti;
    @Max(value = 15)
    private int maxParti;
    @NotNull @NotEmpty @Pattern(regexp = "(http|https)://[a-zA-Z0-9./-]*", message = "http, https로 시작하는 유효한 url이어야 합니다.")
    private String chatUrl;
}
