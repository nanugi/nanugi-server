package com.nanugi.api.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nanugi.api.advice.exception.*;
import com.nanugi.api.config.security.JwtTokenProvider;
import com.nanugi.api.entity.Member;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.MemberJpaRepo;
import com.nanugi.api.service.EmailSenderService;
import com.nanugi.api.service.ResponseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class SignController {

    private final MemberJpaRepo userJpaRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;

    @ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다.")
    @PostMapping(value = "/signin")
    public SingleResult<String> signin(
            @ApiParam(value="이메일, 비밀번호", required=true) @RequestBody @Valid MemberSigninRequest memberSigninRequest) {

        Member member = userJpaRepo.findByUid(memberSigninRequest.getId()).orElseThrow(CEmailSigninFailedException::new);
        if(member.getIsVerified() == false){
            throw new CEmailNotVerifiedException();
        }

        if (!passwordEncoder.matches(memberSigninRequest.getPassword(), member.getPassword()))
            throw new CEmailSigninFailedException();

        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(member.getMsrl()), member.getRoles()));
    }

    @ApiOperation(value = "가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/signup")
    public CommonResult signup(@ApiParam(value = "회원ID : 학교 이메일 / 비밀번호 / 이름 ", required = true) @RequestBody @Valid MemberSignupRequest memberSignupRequest){

        if (userJpaRepo.findByUid(memberSignupRequest.getId()).isPresent()){
            throw new CUserExistException();
        }

        if (userJpaRepo.findByNickname(memberSignupRequest.getNickname()).isPresent()){
            throw new CNicknameAlreadyExistException();
        }

        String code = emailSenderService.getSecretCode();

        try{
            emailSenderService.sendVerificationEmail(memberSignupRequest.getId(), code);
        }
        catch(UnirestException e){
            throw new CEmailSendFailException();
        }

        userJpaRepo.save(Member
                .build(
                        memberSignupRequest.getId(),
                        memberSignupRequest.getNickname(),
                        code,
                        passwordEncoder.encode(memberSignupRequest.getPassword())));

        return responseService.getSuccessResult();
    }

    @ApiOperation(value="이메일 인증", notes="이메일 인증 링크를 클릭한다")
    @GetMapping(value="/email-verification")
    public CommonResult EmailVerification(@ApiParam(value = "이메일 인증 코드", required = true) @RequestParam String code){

        Member user = userJpaRepo.findByVerifyCode(code).orElseThrow(CUserNotFoundException::new);

        user.setIsVerified(true);
        user.setVerifyCode("");
        user.addRole("ROLE_USER");

        userJpaRepo.save(user);

        return responseService.getSuccessResult();
    }

    @ApiOperation(value="비밀번호 찾기 (인증 코드 발송)", notes="이메일을 입력하면 인증 코드를 생성합니다")
    @PostMapping(value="/send-certcode")
    public CommonResult FindPassword(@ApiParam(value="회원 가입 시 이메일", required = true) @RequestBody CertRequest certRequest){

        Member user = userJpaRepo.findByUid(certRequest.getEmail()).orElseThrow(CUserNotFoundException::new);
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
    public CommonResult SetNewPassword(@ApiParam(value="인증 코드", required = true) @Valid @RequestBody NewPassRequest newPassRequest){

        Member user = userJpaRepo.findByCertCode(newPassRequest.getCode()).orElseThrow(CUserNotFoundException::new);
        user.setCertCode("");

        user.setPassword(passwordEncoder.encode(newPassRequest.getPassword()));
        userJpaRepo.save(user);

        return responseService.getSuccessResult("성공적으로 비밀번호가 변경되었습니다.");
    }

    @Data
    @RequiredArgsConstructor
    static class MemberSigninRequest {

        @NotNull @NotEmpty
        String id;

        @NotNull @NotEmpty
        String password;
    }

    @Data
    @RequiredArgsConstructor
    static class MemberSignupRequest {

        @NotNull @NotEmpty @Email
        @Pattern(regexp = "^.*((.ac.kr$)|(.edu$)|(chosun.kr$)|(sangmyung.kr$))", message = "학교 이메일을 입력해주세요. 학교 이메일이 아니라고 계속 뜨는 경우 시스템에 등록되지 않은 학교입니다, 플러스 친구로 문의 or division.foreveryoung@gmail.com 메일을 보내주세요!")
        String id;

        @NotNull @NotEmpty
        @Pattern(regexp = "[0-9a-z!@#$%^*+=-]+", message = "영문, 숫자, 특수문자 !@#$%^*+=-만 사용 가능합니다")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$", message = "8자에서 16자 사이의 문자+숫자(+특수문자)를 조합하는 비밀번호를 만드세요")
        String password;

        @NotNull
        @NotEmpty
        @Size(max = 15, message = "닉네임을 15자 이하로 입력하세요")
        String nickname;
    }

    @Data
    @RequiredArgsConstructor
    static class CertRequest{

        @NotNull @NotEmpty @Email(message = "이메일 형식을 입력하세요")
        String email;
    }

    @Data
    @RequiredArgsConstructor
    static class NewPassRequest {

        @NotNull @NotEmpty @Pattern(regexp = "^[a-z]{24}$", message = "올바른 인증 코드가 아닙니다.")
        String code;

        @NotNull @NotEmpty
        @Pattern(regexp = "[0-9a-z!@#$%^*+=-]+", message = "영문, 숫자, 특수문자 !@#$%^*+=-만 사용 가능합니다")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$", message = "8자에서 16자 사이의 문자+숫자(+특수문자)를 조합하는 비밀번호를 만드세요")
        String password;
    }
}
