package com.nanugi.api.service.board;

import com.nanugi.api.advice.exception.BBoardNotFoundException;
import com.nanugi.api.advice.exception.CResourceNotExistException;
import com.nanugi.api.entity.Post;
import com.nanugi.api.model.dto.PaginatedPostResponse;
import com.nanugi.api.model.dto.PostRequest;
import com.nanugi.api.model.dto.PostResponse;
import com.nanugi.api.model.dto.MemberResponse;
import com.nanugi.api.repo.PostJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostJpaRepo postJpaRepo;

    public Post save(Post post){
        return postJpaRepo.save(post);
    }

    public Post getPost(Long id){
        return postJpaRepo.findById(id).orElseThrow(BBoardNotFoundException::new);
    }

    public void deletePost(Long id){
        postJpaRepo.deleteById(id);
    }

    public PaginatedPostResponse findAllPostsByPage(int page){
        Pageable sortedByCreatedAt =
                PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Post> allPosts = postJpaRepo.findAll(sortedByCreatedAt);

        String next = null;
        String previous = null;

        if(allPosts.hasPrevious()){
            previous = "https://api.nanugi.ml/v1/posts/?page="+(page-1);
        }
        if(allPosts.hasNext()){
            next = "https://api.nanugi.ml/v1/posts/?page="+(page+1);
        }

        List<PostResponse> postResponses =
                allPosts.getContent()
                        .stream()
                        .map(p->(PostResponse.builder().post_id(p.getPost_id()).user(
                                MemberResponse.builder().name(p.getUser().getName()).uid(p.getUser().getUid()).build())
                                .price(p.getPrice())
                                .chatUrl(p.getChatUrl())
                                .content(p.getContent())
                                .maxParti(p.getMaxParti())
                                .minParti(p.getMinParti())
                                .createdAt(p.getCreatedAt())
                                .nanumPrice(p.getNanumPrice())
                                .title(p.getTitle())
                                .build()))
                        .collect(Collectors.toList());

        PaginatedPostResponse paginatedPostResponse
                = PaginatedPostResponse.builder()
                .page(sortedByCreatedAt.getPageNumber())
                .next(next)
                .previous(previous)
                .size(allPosts.getNumberOfElements())
                .posts(postResponses)
                .build();

        return paginatedPostResponse;
    }

    public Post updatePost(Long post_id, PostRequest postRequest) {
        Post post = postJpaRepo.findById(post_id).orElseThrow(CResourceNotExistException::new);
        post.setContent(postRequest.getContent());
        post.setChatUrl(postRequest.getChatUrl());
        post.setMaxParti(postRequest.getMaxParti());
        post.setMinParti(postRequest.getMinParti());
        post.setPrice(postRequest.getTotalPrice());
        post.setNanumPrice(postRequest.getNanumPrice());
        post.setTitle(postRequest.getTitle());
        return post;
    }
}
