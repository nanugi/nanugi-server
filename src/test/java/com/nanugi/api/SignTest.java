package com.nanugi.api;

import com.nanugi.api.advice.exception.CUserNotFoundException;
import com.nanugi.api.entity.Member;
import com.nanugi.api.repo.MemberJpaRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;


@SpringBootTest
public class SignTest {

    @Autowired
    MemberJpaRepo memberJpaRepo;

    @Test
    @Rollback(value = true)
    void SingUpTest(){
    }

    @Test
    void SignInTest(){
        Assertions.assertDoesNotThrow(()->{
            Member member = memberJpaRepo.findById(1L).orElseThrow(CUserNotFoundException::new);

            Assertions.assertEquals(member.getUid(), "test-user@nanugi.ml");
        });
    }
}
