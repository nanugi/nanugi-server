package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.User;
import com.nanugi.api.model.dto.UserResponse;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.UserJpaRepo;
import com.nanugi.api.service.ResponseService;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Api(tags = {"2. User"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class UserController {

    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "내 정보 조회", notes = "내 프로필 정보를 불러온다")
    @GetMapping(value = "/users/me")
    public SingleResult<UserResponse> findMyProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        User user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(
                UserResponse.builder().name(user.getName()).uid(user.getUid()).build());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 단건 조회", notes = "회원번호(msrl)로 회원을 조회한다")
    @GetMapping(value = "/users/{msrl}")
    public SingleResult<UserResponse> findUser( @ApiParam(value = "회원번호", required = true) @PathVariable Long msrl) {

        User user = userJpaRepo.findById(msrl).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(
                UserResponse.builder().name(user.getName()).uid(user.getUid()).build());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 수정", notes = "회원정보를 수정한다")
    @PutMapping(value = "/user")
    public SingleResult<UserResponse> modify(
            @ApiParam(value = "회원이름", required = true) @Valid @RequestBody UserPutRequest userPutRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        User user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        user.setName(userPutRequest.getName());
        user = userJpaRepo.save(user);
        return responseService.getSingleResult(
                UserResponse.builder().name(user.getName()).uid(user.getUid()).build());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "자신의 계정을 삭제한다.")
    @DeleteMapping(value = "/user")
    public CommonResult delete() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        User user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        userJpaRepo.deleteById(user.getMsrl());
        return responseService.getSuccessResult();
    }

    @Data
    @RequiredArgsConstructor
    static class UserPutRequest {
        @NotNull @NotEmpty
        private String name;
    }
}

