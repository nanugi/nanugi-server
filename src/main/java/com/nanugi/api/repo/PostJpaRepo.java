package com.nanugi.api.repo;

import com.nanugi.api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface PostJpaRepo extends JpaRepository<Post, Long> {

    Page<Post> findAllByMember_Msrl(Long msrl, Pageable pageable);

}
