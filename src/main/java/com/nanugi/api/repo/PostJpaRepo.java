package com.nanugi.api.repo;

import com.nanugi.api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;

public interface PostJpaRepo extends JpaRepository<Post, Long> {

}
