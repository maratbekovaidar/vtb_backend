package kz.kalybayevv.VtbNews.web.rest;

import com.codahale.metrics.annotation.Timed;
import kz.kalybayevv.VtbNews.services.ArticleService;
import kz.kalybayevv.VtbNews.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleController {
    private final Logger log = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    /**
     * Find and save News related to user account search by role
     * @return
     */
    @GetMapping("/download")
    @Timed
    public ResponseEntity<?> downloadNews() {
        log.info("Downloading news for User {}", SecurityUtils.getCurrentUserLogin());
        articleService.downloadNews();
        return ResponseEntity.ok("ok");
    }

    /**
     * Find News reletad to user data
     *
     * @return Response (news)
     */
    @GetMapping("/findNews")
    @Timed
    public ResponseEntity<?> findNews(Pageable pageable) {
        log.info("Searching news for User {}", SecurityUtils.getCurrentUserLogin());
        return ResponseEntity.ok(articleService.findNews(pageable));
    }
}
