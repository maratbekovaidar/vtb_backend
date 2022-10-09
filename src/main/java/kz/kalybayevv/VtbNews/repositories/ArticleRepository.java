package kz.kalybayevv.VtbNews.repositories;

import kz.kalybayevv.VtbNews.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Repository;

import java.util.List;

@RedisHash
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByRole_Id(Long id, Pageable pageable);
}
