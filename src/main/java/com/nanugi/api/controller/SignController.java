package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CEmailSigninFailedException;
import com.nanugi.api.advice.exception.CUserExistException;
import com.nanugi.api.config.security.JwtTokenProvider;
import com.nanugi.api.entity.User;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.UserJpaRepo;
import com.nanugi.api.service.ResponseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class SignController {

    private final UserJpaRepo userJpaRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;

    @ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다.")
    @PostMapping(value = "/signin")
    public SingleResult<String> signin(
            @ApiParam(value="아이디(또는 이메일), 비밀번호", required=true) @RequestBody @Valid UserSigninRequest userSigninRequest) {

        User user = userJpaRepo.findByUid(userSigninRequest.getId()).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(userSigninRequest.getPassword(), user.getPassword()))
            throw new CEmailSigninFailedException();

        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles()));
    }

    @ApiOperation(value = "가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/signup")
    public CommonResult signup(@ApiParam(value = "회원ID : 이메일 / 비밀번호 / 이름 ", required = true) @RequestBody @Valid UserSignupRequest userSignupRequest){

        if (userJpaRepo.findByUid(userSignupRequest.getId()).isPresent()){
            throw new CUserExistException();
        }

        userJpaRepo.save(User.builder()
                .uid(userSignupRequest.getId())
                .password(passwordEncoder.encode(userSignupRequest.getPassword()))
                .name(userSignupRequest.getName())
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        return responseService.getSuccessResult();
    }

    @Data
    @RequiredArgsConstructor
    static class UserSigninRequest {

        @NotNull @NotEmpty
        String id;

        @NotNull @NotEmpty
        String password;
    }

    @Data
    @RequiredArgsConstructor
    static class UserSignupRequest {

        @NotNull @NotEmpty
        String id;

        @NotNull @NotEmpty
        String password;

        @NotNull @NotEmpty
        String name;
    }
}
