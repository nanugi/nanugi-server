package com.nanugi.api.service.board;

import com.nanugi.api.advice.exception.CResourceNotExistException;
import com.nanugi.api.entity.Post;
import com.nanugi.api.model.dto.post.*;
import com.nanugi.api.repo.PostJpaRepo;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
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
        return postJpaRepo.findById(id).orElseThrow(CResourceNotExistException::new);
    }

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
            previous = "https://api.nanugi-eco.com/v1/posts?page="+(page-1)+"&search="+keyword;
        }
        if(allPosts.hasNext()){
            next = "https://api.nanugi-eco.com/v1/posts?page="+(page+1)+"&search="+keyword;
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

    public PaginatedPostResponse findAllPostsByPage(int page){
        Pageable sortedByCreatedAt =
                PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Post> allPosts = postJpaRepo.findAll(sortedByCreatedAt);

        String next = null;
        String previous = null;

        if(allPosts.hasPrevious()){
            previous = "https://api.nanugi-eco.com/v1/posts?page="+(page-1);
        }
        if(allPosts.hasNext()){
            next = "https://api.nanugi-eco.com/v1/posts?page="+(page+1);
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

    public PaginatedPostResponse findAllPostsByPageAndMemberId(int page, String nickname, Long member_id){
        Pageable sortedByCreatedAt =
                PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<Post> allPosts = postJpaRepo.findAllByMember_Msrl(member_id, sortedByCreatedAt);

        String next = null;
        String previous = null;

        if(allPosts.hasPrevious()){
            previous = "https://api.nanugi-eco.com/v1/users/posts?"+"nickname="+nickname+"&page="+(page-1);
        }
        if(allPosts.hasNext()){
            next = "https://api.nanugi-eco.com/v1/users/posts?"+"nickname="+nickname+"&page="+(page+1);
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


    public Post closePost(Long post_id){
        Post post = getPost(post_id);
        post.set_close(true);
        return post;
    }
}
