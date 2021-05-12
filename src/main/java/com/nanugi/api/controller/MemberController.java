package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CEmailSigninFailedException;
import com.nanugi.api.advice.exception.CNicknameAlreadyExistException;
import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Member;
import com.nanugi.api.model.dto.MemberResponse;
import com.nanugi.api.model.dto.post.PaginatedPostResponse;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.MemberJpaRepo;
import com.nanugi.api.service.ResponseService;
import com.nanugi.api.service.board.PostService;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Optional;

@Api(tags = {"2. Member(User)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class MemberController {

    private final MemberJpaRepo memberJpaRepo;
    private final PostService postService;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "내 정보 조회", notes = "내 프로필 정보를 불러온다")
    @GetMapping(value = "/users/me")
    public SingleResult<MemberResponse> findMyProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member member = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(member.toMemberResponse());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "내 나눔글 조회", notes = "내가 쓴 나눔글 목록을 조회한다")
    @GetMapping(value = "/users/me/myposts")
    public SingleResult<PaginatedPostResponse> findMyPosts(
            @RequestHeader(name = "X-AUTH-TOKEN") String x_token,
            @ApiParam(value = "페이지", required = true) @RequestParam int page) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member member = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        PaginatedPostResponse myposts = postService.findAllPostsByPageAndMemberId(page, member.getNickname(), member.getMsrl());

        return responseService.getSingleResult(myposts);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 단건 조회", notes = "닉네임으로 회원을 조회한다")
    @GetMapping(value = "/users")
    public SingleResult<MemberResponse> fimdMember(
            @ApiParam(value = "회원 닉네임", required = true) @RequestParam String nickname) {

        Member member = memberJpaRepo.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(member.toBlindMemberResponse());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 나눔글 조회", notes = "닉네임으로 해당 회원이 작성한 나눔글 목록을 조회한다")
    @GetMapping(value = "/users/posts")
    public SingleResult<PaginatedPostResponse> findMemberPosts(
            @RequestHeader(name = "X-AUTH-TOKEN") String x_token,
            @ApiParam(value = "회원 닉네임", required = true) @RequestParam String nickname,
            @ApiParam(value = "페이지", required = true) @RequestParam int page) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Member member = memberJpaRepo.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);

        PaginatedPostResponse posts = postService.findAllPostsByPageAndMemberId(page, nickname, member.getMsrl());

        return responseService.getSingleResult(posts);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 수정", notes = "회원정보를 수정한다")
    @PutMapping(value = "/user")
    public SingleResult<MemberResponse> modify(
            @ApiParam(value = "회원닉네임", required = true) @Valid @RequestBody MemberPutRequest memberPutRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member member = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        Optional<Member> find = memberJpaRepo.findByNickname(memberPutRequest.getNickname());
        if(find.isPresent()){
            throw new CNicknameAlreadyExistException();
        }

        member.setNickname(memberPutRequest.getNickname());
        member = memberJpaRepo.save(member);
        return responseService.getSingleResult(member.toMemberResponse());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "비밀번호 변경", notes = "로그인 되어 있을 경우 자신의 비밀번호를 변경한다")
    @PutMapping(value = "/user/me/password")
    public CommonResult updatePassword(
            @ApiParam(value = "비밀번호 정보(기존 비밀번호, 새 비밀번호)", required = true) @Valid @RequestBody PasswordPutRequest passwordPutRequest){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member member = memberJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        if(passwordEncoder.matches(passwordPutRequest.getPassword(), member.getPassword())){
            member.setPassword(passwordEncoder.encode(passwordPutRequest.getNew_password()));
        }
        else{
            throw new CEmailSigninFailedException();
        }

        return responseService.getSuccessResult();
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
        @Size(max = 15, message = "닉네임은 최대 15자로 구성해야 합니다")
        @Pattern(regexp = "^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|a-z|A-Z|0-9]*", message = "닉네임은 한글, 영문, 숫자로만 이루어질 수 있습니다.")
        private String nickname;
    }

    @Data
    @RequiredArgsConstructor
    static class PasswordPutRequest {
        @NotNull @NotEmpty
        private String password;
        @NotNull @NotEmpty
        @Pattern(regexp = "[0-9a-z!@#$%^*+=-]+", message = "영문, 숫자, 특수문자 !@#$%^*+=-만 사용 가능합니다")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$", message = "8자에서 16자 사이의 문자+숫자(+특수문자)를 조합하는 비밀번호를 만드세요")
        private String new_password;
    }
}

