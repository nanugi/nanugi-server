package com.nanugi.api.repo;

import com.nanugi.api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberJpaRepo extends JpaRepository<Member, Long> {

    Optional<Member> findByUid(String email);

    Optional<Member> findByVerifyCode(String code);

    Optional<Member> findByCertCode(String code);

    Member save(Member user);
}
