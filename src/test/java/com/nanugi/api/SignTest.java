package com.nanugi.api;

import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Member;
import com.nanugi.api.repo.MemberJpaRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class SignTest {

    @Autowired
    MemberJpaRepo memberJpaRepo;

    @Test
    void SingUpTest(){
        Assertions.assertDoesNotThrow(()->{
            Member member = Member.builder()
                    .name("test-name")
                    .uid("test-user2@nanugi.ml")
                    .password("1234qwer")
                    .build();

            Member new_member = memberJpaRepo.save(member);

            Assertions.assertEquals(member.getName(), new_member.getName());
            Assertions.assertEquals(member.getUid(), new_member.getUid());
        });
    }

    @Test
    void SignInTest(){
        Assertions.assertDoesNotThrow(()->{
            Member member = memberJpaRepo.findById(1L).orElseThrow(CUserNotFoundException::new);

            Assertions.assertEquals(member.getUid(), "test-user@nanugi.ml");
        });
    }
}
