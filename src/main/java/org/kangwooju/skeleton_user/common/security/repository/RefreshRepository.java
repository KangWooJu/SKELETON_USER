package org.kangwooju.skeleton_user.common.security.repository;

import org.kangwooju.skeleton_user.common.security.entity.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh,Long> {

    Optional<Refresh> deleteByRefresh(String refresh);
    Boolean existsByRefresh(String refresh);

}
