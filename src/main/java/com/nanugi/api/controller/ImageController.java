package com.nanugi.api.controller;

import com.nanugi.api.advice.exception.*;
import com.nanugi.api.entity.Image;
import com.nanugi.api.entity.Member;
import com.nanugi.api.entity.Post;
import com.nanugi.api.model.dto.image.ImageResponse;
import com.nanugi.api.model.dto.image.PostImageResponse;
import com.nanugi.api.model.response.CommonResult;
import com.nanugi.api.model.response.SingleResult;
import com.nanugi.api.repo.MemberJpaRepo;
import com.nanugi.api.service.ResponseService;
import com.nanugi.api.service.S3Service;
import com.nanugi.api.service.board.ImageService;
import com.nanugi.api.service.board.PostService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"4. Image"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class ImageController {

    private final MemberJpaRepo userJpaRepo;
    private final ImageService imageService;
    private final ResponseService responseService;
    private final PostService postService;
    private final S3Service s3Service;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "이미지 불러오기", notes = "하나의 이미지 조회")
    @GetMapping(value = "/images/{imageId}")
    public SingleResult<Image> findImage(
            @ApiParam(value="이미지 아이디 값", required=true) @PathVariable Long imageId) {

        Image image = imageService.getImage(imageId);

        return responseService.getSingleResult(image);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "이미지 추가", notes = "포스트에 이미지를 추가한다")
    @PostMapping(value = "/posts/{postId}/images")
    public SingleResult<Image> addImage(
            @ApiParam(value = "게시물 아이디 값", required = true) @PathVariable Long postId,
            @ApiParam(value = "이미지 파일", required = true) @RequestPart MultipartFile file){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        Post post = postService.getPost(postId);

        if(post.getUser().getUid() != user.getUid()){
            throw new CNotOwnerException();
        }

        String image_link = "";

        try{
            image_link = s3Service.upload(file, postId);
        }
        catch(Exception e){
            throw new CCommunicationException();
        }

        Image image = Image.builder()
                .image_url(image_link)
                .postId(postId)
                .build();

        imageService.save(image);

        return responseService.getSingleResult(image);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "이미지 삭제", notes = "이미지를 삭제한다")
    @DeleteMapping(value = "/images/{imageId}")
    public CommonResult deleteImage(
            @ApiParam(value = "게시물 아이디 값", required = true) @PathVariable Long imageId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member user = userJpaRepo.findByUid(id).orElseThrow(CUserNotFoundException::new);

        Image image = imageService.getImage(imageId);
        Post post = postService.getPost(image.getPostId());

        if(post.getUser().getUid() != user.getUid()){
            throw new CNotOwnerException();
        }

        imageService.deleteImage(imageId);

        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "포스트에 해당하는 이미지 조회", notes = "포스트에 해당하는 모든 이미지를 조회한다")
    @GetMapping(value = "/posts/{postId}/images")
    public SingleResult<PostImageResponse> findImagesByPost(
            @ApiParam(value = "게시물 아이디 값", required = true) @PathVariable Long postId){

        List<ImageResponse> images = imageService.findImagesByPost(postId)
                .stream()
                .map(i -> ImageResponse.builder()
                        .id(i.getImage_id())
                        .url(i.getImage_url())
                        .build()).collect(Collectors.toList());

        PostImageResponse photoImagesResponse = PostImageResponse.builder()
                .count(images.size())
                .images(images)
                .build();

        return responseService.getSingleResult(photoImagesResponse);
    }
}
