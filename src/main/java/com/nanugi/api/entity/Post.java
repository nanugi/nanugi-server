package com.nanugi.api.entity;

import com.nanugi.api.entity.common.TimeStampedEntity;
import com.nanugi.api.model.dto.image.PostImageResponse;
import com.nanugi.api.model.dto.post.PostListResponse;
import com.nanugi.api.model.dto.post.PostNanumInfoResponse;
import com.nanugi.api.model.dto.post.PostResponse;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Post extends TimeStampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @Column
    @Builder.Default
    private boolean is_close = false;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private List<Image> images= new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Member.class, cascade = CascadeType.MERGE)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1024)
    private String content;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int minParti;

    @Column
    private int maxParti;

    @Column(nullable = false)
    private String chatUrl;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer liked;

    public String getThumbnail(){
        if(images.size() > 0){
            return images.get(0).getImage_url();
        }
        return null;
    }

    public PostNanumInfoResponse toPostNanumInfoResponse(){
        return PostNanumInfoResponse.builder()
                .totalPrice(price)
                .chatUrl(chatUrl)
                .minParti(minParti)
                .maxParti(maxParti)
                .liked(liked)
                .build();
    }

    public PostResponse toPostResponse(){
        return PostResponse.builder()
                .is_close(is_close)
                .detail(toPostNanumInfoResponse())
                .createdAt(getCreatedAt())
                .user(member.toBlindMemberResponse())
                .title(title)
                .content(content)
                .post_id(post_id)
                .build();
    }

    public PostListResponse toPostListResponse(){
        return PostListResponse.builder()
                .post_id(post_id)
                .maxParti(maxParti)
                .minParti(minParti)
                .totalPrice(price)
                .thumbnail(getThumbnail())
                .is_close(is_close)
                .title(title)
                .createdAt(getCreatedAt())
                .nickname(member.getNickname())
                .build();
    }

    public PostImageResponse toPostImageResponse(){
        return PostImageResponse.builder()
                .count(images.size())
                .images(images.stream()
                        .map(i -> i.toImageResponse())
                        .collect(Collectors.toList()))
                .build();
    }

    public static Post build(Member member, String title, String content, int price, int maxParti, int minParti, String chatUrl){
        return Post.builder()
                .member(member)
                .maxParti(maxParti)
                .minParti(minParti)
                .chatUrl(chatUrl)
                .price(price)
                .content(content)
                .title(title)
                .is_close(false)
                .liked(0)
                .build();
    }
}
