package kz.kalybayevv.VtbNews.services.impl;

import kz.kalybayevv.VtbNews.constants.Constants;
import kz.kalybayevv.VtbNews.entity.Article;
import kz.kalybayevv.VtbNews.entity.Role;
import kz.kalybayevv.VtbNews.entity.User;
import kz.kalybayevv.VtbNews.repositories.ArticleRepository;
import kz.kalybayevv.VtbNews.repositories.RoleRepository;
import kz.kalybayevv.VtbNews.repositories.UserRepository;
import kz.kalybayevv.VtbNews.services.ArticleService;
import kz.kalybayevv.VtbNews.utils.SecurityUtils;
import kz.kalybayevv.VtbNews.utils.StringUtils;
import kz.kalybayevv.VtbNews.utils.TrustAllManager;
import kz.kalybayevv.VtbNews.web.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p> Service for parsing data from html
 */
@Service
@RequiredArgsConstructor
public class IArticleService implements ArticleService {
    private final Logger log = LoggerFactory.getLogger(IArticleService.class);

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final RoleRepository roleRepository;

    private final static int REQUEST_CONNECTION_TIMEOUT = 1900000;
    private final static List<Article> articles = new ArrayList<>();


    @Async
    @Override
    public void downloadNews() {
        Optional<User> userFromDb = userRepository.findByEmail(SecurityUtils.getCurrentUserLoginOrThrow());
        if (userFromDb.isPresent()) {
            User user = userFromDb.get();
            Set<Long> ids = user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
            if (ids.stream().findFirst().orElseThrow().equals(Constants.ACCOUNTANT)) {
                try {
                    for (String url : Constants.ACCOUNTANT_NEWS) {
                        for (int i = 1; i <= 800; i++) {
                            String rdyUrl = String.format(url, i);
                            invokeAndSaveAccountant(rdyUrl, ids.stream().findFirst().get());
                        }
                        articleRepository.saveAll(articles);
                    }
                } catch (Exception e) {
                    log.error("error while loading news of accountant account");
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    for (String url : Constants.BUSINESS_NEWS) {
                        JSONArray request = invokeJsonArrayWithSSLTrust(url);
                        log.info("Request Business articles total : {}", request.length());
                        for (int i = 0; i < request.length(); i++) {
                            log.info("Object {}", i);
                            JSONObject item = request.getJSONObject(i);
                            saveArticle(item);
                        }
                    }
                } catch (Exception e) {
                    log.error("error while loading news of business account");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void saveArticle(JSONObject item) throws ParseException {
        Article article = new Article();
        String title = item.optString("title");
        if (StringUtils.isNotEmpty(title) && !"null".equals(title)) {
            article.setTitle(title);
        }
        String description = item.optString("leadin");
        article.setDescription(description);
        long value = item.optLong("publishedAt");
        Timestamp timestamp = new Timestamp(value * 1000);
        Date date = new Date(timestamp.getTime());
        LocalDate createdDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        article.setCreatedDate(createdDate);
        article.setAuthor("Euronews");
        article.setView(BigDecimal.valueOf(Double.parseDouble(item.optString("allViews"))));
        article.setRole(roleRepository.findById(Constants.BUSINESS).get());
        String link = item.optString("canonical");
        article.setLink(link);
        article = articleRepository.save(article);
        log.info("Article {} appeared in database", article.getId());
    }

    private JSONArray invokeJsonArrayWithSSLTrust(String rdyUrl) throws Exception {
        log.info("Fetchind data from URL {}", rdyUrl);
        TrustManager[] trustAllCerts = new TrustManager[]{
                new TrustAllManager()
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());

        URL uri = new URL(rdyUrl);
        HttpsURLConnection con = (HttpsURLConnection) uri.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(REQUEST_CONNECTION_TIMEOUT);
        con.setHostnameVerifier((s, sslSession) -> true);
        con.setSSLSocketFactory(sc.getSocketFactory());
        con.setDoOutput(true);
        InputStream input = con.getInputStream();
        String res = IOUtils.toString(input, String.valueOf(StandardCharsets.UTF_8));
        log.debug("response: {} ", res);
        return new JSONArray(res);
    }

    private void invokeAndSaveAccountant(String rdyUrl, Long roleId) throws IOException {
        Document doc = Jsoup.connect(rdyUrl).get();
        Elements h1elements = doc.getElementsByAttributeValue("class", "feed-item-h2");
        Elements descriptions = doc.getElementsByAttributeValue("class", "feed-item feed-item--normal");
        Optional<Role> role = roleRepository.findById(roleId);
        h1elements.forEach(h1element -> {
            Article article = new Article();
            Element aElement = h1element.child(0);
            String link = aElement.attr("href");
            String title = aElement.text();
            article.setTitle(title);
            article.setLink("https://www.klerk.ru" + link);
            article.setCreatedDate(LocalDate.now(ZoneId.systemDefault()));
            article.setAuthor(rdyUrl.substring(12, 17));
            article.setRole(role.get());
            articles.add(article);
        });
        for (int i = 0; i < descriptions.size(); i++) {
            log.info("Object {}", i);
            if (descriptions.get(i).child(0).childrenSize() > 0) {
                if (descriptions.get(i).child(0).child(0).childrenSize() > 1) {
                    if (descriptions.get(i).child(0).child(0).child(1).childrenSize() > 0) {
                        Element dElement = descriptions.get(i).child(0).child(0).child(1).child(0);
                        articles.get(i).setDescription(dElement.text());
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<?> findNews(Pageable pageable) {
        Optional<User> userFromDb = userRepository.findByEmail(SecurityUtils.getCurrentUserLoginOrThrow());
        User user = userFromDb.get();
        Page<Article> articlePage = articleRepository.findAllByRole_Id(user.getRoles().stream().findFirst().get().getId(), pageable);
        List<Article> articleList = articlePage.toList();
        return ResponseDto.builder()
                .httpStatus(HttpStatus.OK.value())
                .success(Boolean.TRUE)
                .data(new PageImpl<>(articleList, pageable, articleList.size()))
                .build();
    }
}
