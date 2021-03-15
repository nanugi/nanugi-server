package com.nanugi.api.repo;

import com.nanugi.api.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageJpaRepo extends JpaRepository<Image, Long> {
    List<Image> findAllByPostId(Long post_id);
}
