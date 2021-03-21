package com.nanugi.api.entity;

import com.nanugi.api.model.dto.image.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long image_id;

    @Column(nullable = false)
    private String image_url;

    @Column(nullable = false)
    private Long postId;

    public static Image build(Long postId, String image_url){
        return Image.builder()
                .postId(postId)
                .image_url(image_url)
                .build();
    }

    public ImageResponse toImageResponse(){
        return ImageResponse.builder()
                .id(image_id)
                .url(image_url)
                .build();
    }

}
