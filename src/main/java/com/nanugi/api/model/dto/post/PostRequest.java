package com.nanugi.api.model.dto.post;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
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

    @Min(value = 0, message = "0원 이상 입력해야 합니다")
    private int totalPrice;

    @NotNull @Max(value = 20, message = "20명 이하로 입력하세요")
    private int minParti;

    @Max(value = 100, message = "100명 이하로 입력하세요")
    private int maxParti;

    @NotNull @NotEmpty @Pattern(regexp = "(http|https)://[a-zA-Z0-9./-]*", message = "http, https로 시작하는 유효한 url이어야 합니다.")
    private String chatUrl;
}
