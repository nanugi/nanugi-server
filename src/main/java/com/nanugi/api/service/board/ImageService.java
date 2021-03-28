package com.nanugi.api.service.board;

import com.nanugi.api.advice.exception.CResourceNotExistException;
import com.nanugi.api.entity.Image;
import com.nanugi.api.repo.ImageJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final ImageJpaRepo imageJpaRepo;

    public Image save(Image image){
        return imageJpaRepo.save(image);
    }

    @Cacheable(value = "get_image", key = "#id")
    public Image getImage(Long id){
        return imageJpaRepo.findById(id).orElseThrow(CResourceNotExistException::new);
    }

    @CacheEvict(value = "get_image", key = "#id")
    public void deleteImage(Long id){
        imageJpaRepo.deleteById(id);
    }

}
