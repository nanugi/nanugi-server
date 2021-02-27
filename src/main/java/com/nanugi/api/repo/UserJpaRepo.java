package com.nanugi.api.repo;

import com.nanugi.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserJpaRepo extends JpaRepository<User, Long> {

    Optional<User> findByUid(String email);

    Optional<User> findByVerifyCode(String code);

    User save(User user);

}
