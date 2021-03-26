package com.nanugi.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nanugi.api.entity.common.TimeStampedEntity;
import com.nanugi.api.model.dto.MemberResponse;
import lombok.*;
import org.hibernate.annotations.Proxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder // builder를 사용할수 있게 합니다.
@Entity // jpa entity임을 알립니다.
@Getter // user 필드값의 getter를 자동으로 생성합니다.
@Setter
@NoArgsConstructor // 인자없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor // 인자를 모두 갖춘 생성자를 자동으로 생성합니다.
@Table(name = "member") // 'user' 테이블과 매핑됨을 명시
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Post Entity에서 User와의 관계를 Json으로 변환시 오류 방지를 위한 코드
@Proxy(lazy = false)
public class Member extends TimeStampedEntity implements UserDetails {
    @Id // pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long msrl;

    @Column(nullable = false, unique = true, length = 50)
    private String uid;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 15, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String verifyCode;

    @Column(nullable = false)
    private Boolean isVerified;

    @Column
    private String certCode;

    @Column(length = 100)
    private String provider;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(targetEntity = Post.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_favs")
    private List<Post> favs = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public void addRole(String role){
        roles.add(role);
    }

    public String getBlindUid(){
        return uid.substring(0, 1) + "*****" + uid.substring(uid.indexOf("@"));
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.uid;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }

    public MemberResponse toBlindMemberResponse(){
        return MemberResponse.builder()
                .uid(getBlindUid())
                .nickname(nickname)
                .build();
    }

    public MemberResponse toMemberResponse(){
        return MemberResponse.builder()
                .uid(uid)
                .nickname(nickname)
                .build();
    }

    public void toggleFav(Post post){
        for(Post p: favs){
            if(p.getPost_id() == post.getPost_id()){
                favs.remove(post);
                return;
            }
        }
        favs.add(post);
    }

    public static Member build(String uid, String nickname, String verifyCode, String encodedPassword){
        return Member.builder()
                .uid(uid)
                .nickname(nickname)
                .password(encodedPassword)
                .isVerified(false)
                .verifyCode(verifyCode)
                .build();
    }
}
