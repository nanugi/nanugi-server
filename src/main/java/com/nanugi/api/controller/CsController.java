package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CEmailSendFailException;
import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Member;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.service.EmailSenderService;
import com.nanugi.api.service.ResponseService;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Api(tags = {"9. Cs"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class CsController {

    private final EmailSenderService emailSenderService;
    private final ResponseService responseService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value="고객 문의", notes="문의 사항 접수 후 나누기 지메일로 해당 문의를 보냅니다. 주의사항 : 연락받을 이메일, 핸드폰 번호(000-0000-0000 형식)가 필요합니다.")
    @PostMapping(value="/cs/new")
    public CommonResult SendCsEmail(@ApiParam(value = "이메일 인증 코드", required = true) @Valid @RequestBody CsRequest csRequest){

        try {
            emailSenderService.sendCsEmail(csRequest.getEmail(), csRequest.getPhone_number(), csRequest.getContent());
        }
        catch(Exception e){
            throw new CEmailSendFailException();
        }

        return responseService.getSuccessResult();
    }

    @Data
    static class CsRequest{
        @NotNull @NotEmpty @Email(message = "유효한 이메일을 입력해주세요")
        String email;
        @NotNull @NotEmpty @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$", message = "유효한 핸드폰 번호를 ###-####-#### 형식으로 입력해주세요.")
        String phone_number;
        @NotNull @NotEmpty @Size(max = 8000, message = "최대 8000자까지 입력 가능합니다.")
        String content;
    }
}
