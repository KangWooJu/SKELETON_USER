package org.kangwooju.skeleton_user.domain.user.repository;

import org.kangwooju.skeleton_user.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);
}
