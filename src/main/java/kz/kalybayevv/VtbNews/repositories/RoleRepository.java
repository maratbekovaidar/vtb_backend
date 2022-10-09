package kz.kalybayevv.VtbNews.repositories;

import kz.kalybayevv.VtbNews.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Repository;

@RedisHash
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
