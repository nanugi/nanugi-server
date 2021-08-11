package com.nanugi.api.repo;

import com.nanugi.api.advice.exception.CResourceNotExistException;
import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Member;
import com.nanugi.api.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostJpaRepoTest {

    @Autowired
    private PostJpaRepo postJpaRepo;
    @Autowired
    private MemberJpaRepo memberJpaRepo;

    @Test
    public void find_post_by_id(){
        Post post = postJpaRepo.findById(1L).orElseThrow(CResourceNotExistException::new);
        assertEquals(post.getPost_id(), 1L);
        assertEquals(post.getTitle(), "나누기를 시작하세요!");
        assertEquals(post.getMember().getNickname(), "나누기");
    }

    @Test
    public void save_post(){
        Member member = memberJpaRepo.findById(1L).orElseThrow(CUserNotFoundException::new);
        Post post = Post.build(
                member,
                "title",
                "contentcontentcontentcontent",
                10000,
                3,
                1,
                "http://nanugi-eco.com/");
        Post saved_post = postJpaRepo.findById(postJpaRepo.save(post).getPost_id()).orElseThrow(CResourceNotExistException::new);

        LocalDateTime now = LocalDateTime.now();

        assertEquals(member.getUid(), saved_post.getMember().getUid());
        assertEquals(post.getTitle(), saved_post.getTitle());
        assertEquals(post.getContent(), saved_post.getContent());
        assertEquals(post.getChatUrl(), saved_post.getChatUrl());
        assertEquals(0, saved_post.getLiked());
        assertEquals(0, saved_post.getImages().size());
        assertEquals(post.getMaxParti(), saved_post.getMaxParti());
        assertEquals(post.getMinParti(), saved_post.getMinParti());
        assertEquals( 0, saved_post.getView());
        assertEquals(false, saved_post.is_close());
        assertTrue(saved_post.getCreatedAt().isBefore(now));
        assertTrue(saved_post.getCreatedAt().plusSeconds(1).isAfter(now));
    }

}