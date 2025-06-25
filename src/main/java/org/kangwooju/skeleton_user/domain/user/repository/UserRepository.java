package org.kangwooju.skeleton_user.domain.user.repository;

import org.kangwooju.skeleton_user.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.nickname = :nickname where u.id = :id")
    String updateUserById(@Param("id") Long id,@Param("nickname") String nickname);
}
