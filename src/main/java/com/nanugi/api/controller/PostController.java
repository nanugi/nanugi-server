package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CNotOwnerException;
import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Post;
import com.nanugi.api.entity.Member;
import com.nanugi.api.model.dto.post.PaginatedPostResponse;
import com.nanugi.api.model.dto.post.PostRequest;
import com.nanugi.api.model.dto.post.PostResponse;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.MemberJpaRepo;
import com.nanugi.api.service.ResponseService;
import com.nanugi.api.service.board.PostService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"3. Post"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PostController {
    private final MemberJpaRepo userJpaRepo;
    private final PostService postService;
    private final ResponseService responseService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 글 조회", notes = "모든 글을 조회한다 (기본적으로 최신 순으로 정렬됩니다, 0페이지 부터 10개씩 보여줍니다.)")
    @GetMapping(value = "/posts")
    public SingleResult<PaginatedPostResponse> findAllPosts(@ApiParam(value = "페이지", required = true) @RequestParam int page) {
        PaginatedPostResponse paginatedPostResponse = postService.findAllPostsByPage(page);

        return responseService.getSingleResult(paginatedPostResponse);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "글 상세 조회", notes = "하나의 글을 조회한다")
    @GetMapping(value = "/posts/{id}")
    public SingleResult<PostResponse> findPost(@ApiParam(value = "포스트 아이디", required = true) @PathVariable Long id) {
        Post post = postService.getPost(id);

        return responseService.getSingleResult(post.toPostResponse());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "글 삭제", notes = "하나의 글을 삭제한다")
    @DeleteMapping(value = "/posts/{post_id}")
    public CommonResult deletePost(@ApiParam(value = "포스트 아이디", required = true) @PathVariable Long post_id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);
        Post post = postService.getPost(post_id);

        if(post.getUser().getMsrl() != user.getMsrl()){
            throw new CNotOwnerException();
        }

        postService.deletePost(post_id);

        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "글 수정", notes = "하나의 글을 수정한다")
    @PutMapping(value = "/posts/{post_id}")
    public SingleResult<PostResponse> updatePost(@ApiParam(value = "포스트 아이디", required = true) @PathVariable Long post_id,
                                                 @ApiParam(value = "포스트 내용", required = true) @Valid @RequestBody PostRequest postRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);
        Post post = postService.getPost(post_id);

        if(post.getUser().getMsrl() != user.getMsrl()){
            throw new CNotOwnerException();
        }

        Post new_post = postService.updatePost(post_id, postRequest);

        return responseService.getSingleResult(new_post.toPostResponse());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "나누기 종료", notes = "나눔 상태를 종료로 바꾼다")
    @PutMapping(value = "/posts/{post_id}/close")
    public SingleResult<PostResponse> updatePost(@ApiParam(value = "포스트 아이디", required = true) @PathVariable Long post_id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);
        Post post = postService.getPost(post_id);

        if(post.getUser().getMsrl() != user.getMsrl()){
            throw new CNotOwnerException();
        }

        post.set_close(true);
        Post new_post = postService.save(post);

        return responseService.getSingleResult(new_post.toPostResponse());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "글 쓰기", notes = "글을 작성한다")
    @PostMapping(value = "/posts")
    public SingleResult<PostResponse> savePost(@ApiParam(value = "글", required = true) @Valid @RequestBody PostRequest postRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        Post post = Post.build(user, postRequest.getTitle(), postRequest.getContent(), postRequest.getNanumPrice(), postRequest.getTotalPrice(), postRequest.getMaxParti(), postRequest.getMinParti(), postRequest.getChatUrl());

        Post savedPost = postService.save(post);

        return responseService.getSingleResult(savedPost.toPostResponse());
    }
}
