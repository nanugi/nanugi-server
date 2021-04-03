package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CAuthenticationEntryPointException;
import com.nanugi.api.advice.exception.CUserNotFoundException;;
import com.nanugi.api.entity.Member;
import com.nanugi.api.entity.Post;
import com.nanugi.api.model.dto.post.PostListResponse;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.ListResult;
import com.nanugi.api.repo.MemberJpaRepo;
import com.nanugi.api.service.ResponseService;
import com.nanugi.api.service.board.PostService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"5. Favs"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "v1/")
public class FavsController {

    private final PostService postService;
    private final ResponseService responseService;
    private final MemberJpaRepo memberJpaRepo;

    @Cacheable(value = "get_myfavs", key = "#x_token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "관심 목록 불러오기", notes = "나의 관심목록 조회")
    @GetMapping(value = "/favs")
    public ListResult<PostListResponse> favList(
            @RequestHeader(name = "X-AUTH-TOKEN") String x_token
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member user = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        List<PostListResponse> postLists = user.getFavs().stream().map(p->p.toPostListResponse()).collect(Collectors.toList());

        return responseService.getListResult(postLists);
    }

    @CacheEvict(value = "get_myfavs", key = "#x_token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "관심목록 추가하기/없애기", notes = "관심목록 추가 or 삭제 (토글형식)")
    @PutMapping(value = "/favs")
    public CommonResult toggleFav(
            @RequestHeader(name = "X-AUTH-TOKEN") String x_token,
            @ApiParam(value = "게시물 아이디 값", required = true) @RequestParam Long postId
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member user = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        Post post = postService.getPost(postId);

        if(post.getMember().getUid() == id){
            throw new CAuthenticationEntryPointException();
        }

        String message = user.toggleFav(post);
        memberJpaRepo.save(user);
        postService.save(post);

        return responseService.getSuccessResult(message);
    }

}
