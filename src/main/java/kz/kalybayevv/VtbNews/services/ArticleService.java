package kz.kalybayevv.VtbNews.services;

import kz.kalybayevv.VtbNews.web.dto.ResponseDto;
import org.springframework.data.domain.Pageable;

public interface ArticleService {
    void downloadNews();

    ResponseDto<?> findNews(Pageable pageable);
}
