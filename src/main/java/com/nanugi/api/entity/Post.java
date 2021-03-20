package com.nanugi.api.entity;

import com.nanugi.api.entity.common.TimeStampedEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Member.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "msrl")
    private Member user;

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

    public String getThumbnail(){
        if(images.size() > 0){
            return images.get(0).getImage_url();
        }
        return null;
    }
}
