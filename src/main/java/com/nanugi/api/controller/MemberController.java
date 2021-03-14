package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Member;
import com.nanugi.api.model.dto.MemberResponse;
import com.nanugi.api.model.dto.post.PostNanumInfoResponse;
import com.nanugi.api.model.dto.post.PostResponse;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.ListResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.MemberJpaRepo;
import com.nanugi.api.service.ResponseService;
import com.nanugi.api.service.board.PostService;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"2. Member(User)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class MemberController {

    private final MemberJpaRepo memberJpaRepo;
    private final ResponseService responseService;
    private final PostService postService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "내 정보 조회", notes = "내 프로필 정보를 불러온다")
    @GetMapping(value = "/users/me")
    public SingleResult<MemberResponse> findMyProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member member = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(
                MemberResponse.builder().name(member.getName()).uid(member.getUid()).build());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "내 나눔글 조회", notes = "내가 쓴 나눔글 목록을 조회한다")
    @GetMapping(value = "/users/me/myposts")
    public ListResult<PostResponse> findMyPosts() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member member = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);
        List<PostResponse> myposts = member.getPosts().stream().map(p-> PostResponse.builder()
                .post_id(p.getPost_id())
                .title(p.getTitle())
                .content(p.getContent())
                .createdAt(p.getCreatedAt())
                .user(MemberResponse.builder()
                        .name(member.getName())
                        .uid(member.getUid())
                        .build())
                .detail(PostNanumInfoResponse.builder()
                        .price(p.getPrice())
                        .nanumPrice(p.getNanumPrice())
                        .chatUrl(p.getChatUrl())
                        .maxParti(p.getMaxParti())
                        .minParti(p.getMinParti())
                        .build())
                .is_close(p.is_close())
                .build()).collect(Collectors.toList());

        return responseService.getListResult(myposts);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 단건 조회", notes = "회원번호(msrl)로 회원을 조회한다")
    @GetMapping(value = "/users/{msrl}")
    public SingleResult<MemberResponse> fimdMember(@ApiParam(value = "회원번호", required = true) @PathVariable Long msrl) {

        Member member = memberJpaRepo.findById(msrl).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(
                MemberResponse.builder().name(member.getName()).uid(member.getUid()).build());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 수정", notes = "회원정보를 수정한다")
    @PutMapping(value = "/user")
    public SingleResult<MemberResponse> modify(
            @ApiParam(value = "회원이름", required = true) @Valid @RequestBody MemberPutRequest memberPutRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member member = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        member.setName(memberPutRequest.getName());
        member = memberJpaRepo.save(member);
        return responseService.getSingleResult(
                MemberResponse.builder().name(member.getName()).uid(member.getUid()).build());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "자신의 계정을 삭제한다.")
    @DeleteMapping(value = "/user")
    public CommonResult deleteMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member member = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        memberJpaRepo.deleteById(member.getMsrl());
        return responseService.getSuccessResult();
    }

    @Data
    @RequiredArgsConstructor
    static class MemberPutRequest {
        @NotNull @NotEmpty
        private String name;
    }
}

