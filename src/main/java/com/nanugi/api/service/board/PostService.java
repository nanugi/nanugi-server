package com.nanugi.api.service.board;

import com.nanugi.api.advice.exception.BBoardNotFoundException;
import com.nanugi.api.entity.Post;
import com.nanugi.api.repo.PostJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostJpaRepo postJpaRepo;

    public Post save(Post board){
        return postJpaRepo.save(board);
    }

    public Post getBoard(Long id){
        return postJpaRepo.findById(id).orElseThrow(BBoardNotFoundException::new);
    }

    public List<Post> findAllPostsByPage(int page){
        Pageable sortedByCreatedAt =
                PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Post> allPosts = postJpaRepo.findAll(sortedByCreatedAt);
        return allPosts.getContent();
    }
}
