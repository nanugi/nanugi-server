package com.nanugi.api.entity;

import com.nanugi.api.entity.common.TimeStampedEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@RequiredArgsConstructor
public class Post extends TimeStampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "msrl")
    private User user;

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
    private int nanumPrice;

    @Column(nullable = false)
    private String chatUrl;

    @Builder
    public Post(User user, String title, String content, int price, int minParti, int maxParti, int nanumPrice, String chatUrl){
        this.user = user;
        this.title = title;
        this.content = content;
        this.price = price;
        this.maxParti = maxParti;
        this.minParti = minParti;
        this.nanumPrice = nanumPrice;
        this.chatUrl = chatUrl;
    }

    @Builder
    public Post(User user, String title, String content, int price, int minParti, int nanumPrice, String chatUrl){
        this.user = user;
        this.title = title;
        this.content = content;
        this.price = price;
        this.minParti = minParti;
        this.nanumPrice = nanumPrice;
        this.chatUrl = chatUrl;
    }
}
