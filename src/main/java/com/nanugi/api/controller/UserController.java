package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.CAuthenticationEntryPointException;
import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.User;
import com.nanugi.api.model.dto.UserResponse;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.ListResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.UserJpaRepo;
import com.nanugi.api.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"2. User"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class UserController {

    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService; // 결과를 처리할 Service

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 리스트 조회", notes = "모든 회원을 조회한다")
    @GetMapping(value = "/users")
    public ListResult<UserResponse> findAllUser() {
        // 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
        List<User> users = userJpaRepo.findAll();
        List<UserResponse> res = users.stream()
                .map(u->new UserResponse(u.getName(), u.getUid()))
                .collect(Collectors.toList());
        return responseService.getListResult(res);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 단건 조회", notes = "회원번호(msrl)로 회원을 조회한다")
    @GetMapping(value = "/user")
    public SingleResult<UserResponse> findUser() {
        // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        // 결과데이터가 단일건인경우 getSingleResult를 이용해서 결과를 출력한다.
        User user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(new UserResponse(user.getName(), user.getUid()));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 수정", notes = "회원정보를 수정한다")
    @PutMapping(value = "/user")
    public SingleResult<UserResponse> modify(
            @ApiParam(value = "회원이름", required = true) @RequestParam String name) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        User user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        user.setName(name);
        user = userJpaRepo.save(user);
        return responseService.getSingleResult(new UserResponse(user.getName(), user.getUid()));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "회원번호(msrl)로 회원정보를 삭제한다")
    @DeleteMapping(value = "/user/{msrl}")
    public CommonResult delete(
            @ApiParam(value = "회원번호", required = true) @PathVariable long msrl) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        User user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        if(user.getMsrl() != msrl){
            throw new CAuthenticationEntryPointException();
        }

        userJpaRepo.deleteById(msrl);
        return responseService.getSuccessResult();
    }
}

