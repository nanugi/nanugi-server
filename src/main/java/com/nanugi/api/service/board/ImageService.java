package com.nanugi.api.service.board;

import com.nanugi.api.advice.exception.CResourceNotExistException;
import com.nanugi.api.entity.Image;
import com.nanugi.api.repo.ImageJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageJpaRepo imageJpaRepo;

    public Image save(Image image){
        return imageJpaRepo.save(image);
    }

    public Image getImage(Long id){
        return imageJpaRepo.findById(id).orElseThrow(CResourceNotExistException::new);
    }

    public void deleteImage(Long id){
        imageJpaRepo.deleteById(id);
    }

    public List<Image> findImagesByPost(Long post_id) {return imageJpaRepo.findAllByPostId(post_id);}
}
