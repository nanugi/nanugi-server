package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Post;
import com.nanugi.api.entity.User;
import com.nanugi.api.model.dto.PostResponse;
import com.nanugi.api.model.dto.UserResponse;
import com.nanugi.api.model.response.ListResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.UserJpaRepo;
import com.nanugi.api.service.ResponseService;
import com.nanugi.api.service.board.PostService;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"3. Post"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PostController {
    private final UserJpaRepo userJpaRepo;
    private final PostService postService;
    private final ResponseService responseService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 글 조회", notes = "모든 글을 조회한다")
    @GetMapping(value = "/posts")
    public ListResult<PostResponse> findAllPosts(@ApiParam(value = "페이지", required = true) @RequestParam int page) {
        List<Post> posts = postService.findAllPostsByPage(page);

        List<PostResponse> res = posts.stream()
                .map(p->new PostResponse(p.getBoard_id(),
                        new UserResponse(p.getUser().getName(), p.getUser().getUid()),
                        p.getTitle(), p.getContent(), p.getPrice(), p.getNanumPrice(),
                        p.getChatUrl(), p.getMinParti(), p.getMaxParti()))
                .collect(Collectors.toList());

        return responseService.getListResult(res);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "글 쓰기", notes = "글을 작성한다")
    @PostMapping(value = "/posts")
    public SingleResult<PostResponse> savePost(@ApiParam(value = "글", required = true) @Valid @RequestBody PostRequest postRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        User user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        Post.PostBuilder postBuilder = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .chatUrl(postRequest.getChatUrl())
                .minParti(postRequest.getMinParti())
                .maxParti(postRequest.getMaxParti())
                .price(postRequest.getPrice())
                .user(user)
                .nanumPrice(postRequest.getPrice() / (postRequest.getMinParti() + 1));

        Post post = postBuilder.build();

        Post savedPost = postService.save(post);

        return responseService.getSingleResult(new PostResponse(
                savedPost.getBoard_id(),
                new UserResponse(user.getName(),user.getUid()),
                savedPost.getTitle(),
                savedPost.getContent(),
                savedPost.getPrice(),
                savedPost.getNanumPrice(),
                savedPost.getChatUrl(),
                savedPost.getMinParti(),
                savedPost.getMaxParti()
                ));

    }

    @Data
    static class PostRequest {
        @NotNull @NotEmpty @Length(max = 50)
        private String title;
        @NotNull @NotEmpty @Length(max = 800)
        private String content;
        @NotNull
        private int price;
        @NotNull @Min(value = 1) @Max(value = 10)
        private int minParti;
        @Max(value = 15)
        private int maxParti;
        @NotNull @NotEmpty @Pattern(regexp = "(http|https)://[a-zA-Z0-9./-]*")
        private String chatUrl;
    }
}
