package com.nanugi.api.repo;

import com.nanugi.api.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepo extends JpaRepository<Post, Long> {

}
