package com.nanugi.api.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nanugi.api.advice.exception.*;
import com.nanugi.api.config.security.JwtTokenProvider;
import com.nanugi.api.entity.User;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.UserJpaRepo;
import com.nanugi.api.service.EmailSenderService;
import com.nanugi.api.service.ResponseService;

import com.nanugi.api.service.user.CustomUserDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Random;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class SignController {

    private final UserJpaRepo userJpaRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final CustomUserDetailService customUserDetailService;

    @ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다.")
    @PostMapping(value = "/signin")
    public SingleResult<String> signin(
            @ApiParam(value="이메일, 비밀번호", required=true) @RequestBody @Valid UserSigninRequest userSigninRequest) {

        User user = userJpaRepo.findByUid(userSigninRequest.getId()).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(userSigninRequest.getPassword(), user.getPassword()))
            throw new CEmailSigninFailedException();

        if(user.getIsVerified() == false){
            throw new CEmailNotVerifiedException();
        }

        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles()));
    }

    @ApiOperation(value = "가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/signup")
    public CommonResult signup(@ApiParam(value = "회원ID : 학교 이메일 / 비밀번호 / 이름 ", required = true) @RequestBody @Valid UserSignupRequest userSignupRequest){

        if (userJpaRepo.findByUid(userSignupRequest.getId()).isPresent()){
            throw new CUserExistException();
        }

        String code = emailSenderService.getSecretCode();

        try{
            emailSenderService.sendVerificationEmail(userSignupRequest.getId(), code);
        }
        catch(UnirestException e){
            throw new CEmailSendFailException();
        }

        userJpaRepo.save(User.builder()
                .uid(userSignupRequest.getId())
                .password(passwordEncoder.encode(userSignupRequest.getPassword()))
                .name(userSignupRequest.getName())
                .isVerified(false)
                .verifyCode(code)
                .certCode("")
                .build());

        return responseService.getSuccessResult();
    }

    @ApiOperation(value="이메일 인증", notes="이메일 인증 링크를 클릭한다")
    @GetMapping(value="/email-verification")
    public CommonResult EmailVerification(@ApiParam(value = "이메일 인증 코드", required = true) @RequestParam String code){

        User user = userJpaRepo.findByVerifyCode(code).orElseThrow(CUserNotFoundException::new);

        user.setIsVerified(true);
        user.setVerifyCode("");
        user.addRole("ROLE_USER");

        userJpaRepo.save(user);

        return responseService.getSuccessResult();
    }

    @ApiOperation(value="비밀번호 찾기 (인증 코드 발송)", notes="이메일을 입력하면 인증 코드를 생성합니다")
    @PostMapping(value="/send-certcode")
    public CommonResult FindPassword(@ApiParam(value="회원 가입 시 이메일", required = true) @RequestBody String email){

        User user = userJpaRepo.findByUid(email).orElseThrow(CUserNotFoundException::new);
        if(!user.getIsVerified()){
            throw new CEmailNotVerifiedException();
        }

        String code = emailSenderService.getSecretCode();

        user.setCertCode(code);
        user = userJpaRepo.save(user);

        try {
            emailSenderService.sendCertifyEmail(user.getUid(), user.getCertCode());
        }
        catch(UnirestException e){
            throw new CEmailSendFailException();
        }

        return responseService.getSuccessResult();
    }

    @ApiOperation(value="비밀번호 찾기 (인증 코드 확인, 비밀번호 변경)", notes="인증 코드 입력 후 비밀번호를 변경합니다")
    @PostMapping(value="/set-new-password")
    public CommonResult SetNewPassword(@ApiParam(value="인증 코드", required = true) @RequestBody NewPassRequest newPassRequest){

        User user = userJpaRepo.findByCertCode(newPassRequest.getCode()).orElseThrow(CUserNotFoundException::new);
        user.setCertCode("");

        user.setPassword(passwordEncoder.encode(newPassRequest.getPassword()));
        userJpaRepo.save(user);

        return responseService.getSuccessResult("성공적으로 비밀번호가 변경되었습니다.");
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

        @NotNull @NotEmpty @Email @Pattern(regexp = "^.*((.ac.kr$)|(.edu$))", message = "학교 이메일을 입력해주세요.")
        String id;

        @NotNull @NotEmpty
        String password;

        @NotNull @NotEmpty
        String name;
    }

    @Data
    @RequiredArgsConstructor
    static class NewPassRequest {

        @NotNull @NotEmpty @Pattern(regexp = "[a-z]{24}", message = "올바른 인증 코드가 아닙니다.")
        String code;

        @NotNull @NotEmpty
        String password;
    }
}
