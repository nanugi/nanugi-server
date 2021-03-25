package com.nanugi.api.service;

import com.nanugi.api.advice.exception.CCommunicationException;
import com.nanugi.api.entity.Image;
import com.nanugi.api.service.board.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AsyncService {

    private final S3Service s3Service;
    private final ImageService imageService;

    @Async
    public void uploadAndSaveImage(MultipartFile file, Long postId){

        String image_link = null;

        try{
            image_link = s3Service.upload(file, postId);
            Image image = Image.build(postId, image_link);

            imageService.save(image);
        }
        catch(IOException e){
            try{
                image_link = s3Service.upload(file, postId);
                Image image = Image.build(postId, image_link);

                imageService.save(image);
            }
            catch(IOException e2){
                throw new CCommunicationException();
            }
        }
    }
}
