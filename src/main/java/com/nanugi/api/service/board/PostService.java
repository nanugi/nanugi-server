package com.nanugi.api.service.board;

import com.nanugi.api.advice.exception.CResourceNotExistException;
import com.nanugi.api.entity.Post;
import com.nanugi.api.model.dto.post.*;
import com.nanugi.api.repo.PostJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Cacheable(value = "get_post", key = "#id", unless = "#result == null")
    public Post getPost(Long id){
        return postJpaRepo.findById(id).orElseThrow(CResourceNotExistException::new);
    }

    @Caching(evict = {
        @CacheEvict(value = "get_posts", allEntries = true),
        @CacheEvict(value = "get_post", key = "#id")
    })
    public void deletePost(Long id){
        postJpaRepo.deleteById(id);
    }

    public PaginatedPostResponse findAllPostsByKeyword(int page, String keyword){
        Pageable sortedByCreatedAt =
                PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Post> allPosts = postJpaRepo.findAllByTitleContaining(keyword, sortedByCreatedAt);

        String next = null;
        String previous = null;

        if(allPosts.hasPrevious()){
            previous = "https://api.nanugi.ml/v1/posts/?page="+(page-1)+"&search="+keyword;
        }
        if(allPosts.hasNext()){
            next = "https://api.nanugi.ml/v1/posts/?page="+(page+1)+"&search="+keyword;
        }

        List<PostListResponse> postResponses =
                allPosts.getContent()
                        .stream()
                        .map(p->p.toPostListResponse())
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

    @Cacheable(value = "get_posts", key = "#page")
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

        List<PostListResponse> postResponses =
                allPosts.getContent()
                        .stream()
                        .map(p->p.toPostListResponse())
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

    public PaginatedPostResponse findAllPostsByPageAndMemberId(int page, Long member_id){
        Pageable sortedByCreatedAt =
                PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Post> allPosts = postJpaRepo.findAllByMember_Msrl(member_id, sortedByCreatedAt);

        String next = null;
        String previous = null;

        if(allPosts.hasPrevious()){
            previous = "https://api.nanugi.ml/v1/posts/?page="+(page-1);
        }
        if(allPosts.hasNext()){
            next = "https://api.nanugi.ml/v1/posts/?page="+(page+1);
        }

        List<PostListResponse> postResponses =
                allPosts.getContent()
                        .stream()
                        .map(p->p.toPostListResponse())
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

    @CacheEvict(value = "get_posts", allEntries = true)
    @CachePut(value = "get_post", key = "#post_id")
    public Post updatePost(Long post_id, PostRequest postRequest) {
        Post post = getPost(post_id);
        post.setContent(postRequest.getContent());
        post.setChatUrl(postRequest.getChatUrl());
        post.setMaxParti(postRequest.getMaxParti());
        post.setMinParti(postRequest.getMinParti());
        post.setPrice(postRequest.getTotalPrice());
        post.setTitle(postRequest.getTitle());
        return post;
    }

    @CacheEvict(value = "get_posts", allEntries = true)
    @CachePut(value = "get_post", key = "#post_id")
    public Post closePost(Long post_id){
        Post post = getPost(post_id);
        post.set_close(true);
        return post;
    }
}
