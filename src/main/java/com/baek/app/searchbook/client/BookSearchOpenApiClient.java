package com.baek.app.searchbook.client;

import com.baek.app.searchbook.client.entity.kakao.Document;
import com.baek.app.searchbook.client.entity.kakao.KakaoBookEntity;
import com.baek.app.searchbook.client.entity.naver.Item;
import com.baek.app.searchbook.client.entity.naver.NaverBookEntity;
import com.baek.app.searchbook.dto.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Component
public class BookSearchOpenApiClient {
    private final String kakaoUrl;
    private final String kakaoAppKey;
    private final String naverUrl;
    private final String naverClientId;
    private final String naverClientSecret;

    private final RestTemplate kakaoTemplate;
    private final RestTemplate naverTemplate;

    public BookSearchOpenApiClient(
            @Value("${application.book-search-api.kakao.url}") String kakaoUrl,
            @Value("${application.book-search-api.kakao.app-key}") String kakaoAppKey,
            @Value("${application.book-search-api.naver.url}") String naverUrl,
            @Value("${application.book-search-api.naver.client-id}") String naverClientId,
            @Value("${application.book-search-api.naver.client-secret}") String naverClientSecret,
            @Qualifier("kakaoTemplate") RestTemplate kakaoTemplate,
            @Qualifier("naverTemplate") RestTemplate naverTemplate
    ) {
        this.kakaoUrl = kakaoUrl;
        this.kakaoAppKey = kakaoAppKey;
        this.naverClientId = naverClientId;
        this.naverClientSecret = naverClientSecret;
        this.naverUrl = naverUrl;

        this.kakaoTemplate = kakaoTemplate;
        this.naverTemplate = naverTemplate;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(1000))
    public List<Book> callKakaoApi(@NotEmpty String query, @Min(1) @Max(50) int page, @Min(1) @Max(50) int size) throws Exception {
        String url = kakaoUrl + "?query=" + query + "&page=" + page + "&size=" + size;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + kakaoAppKey);
        KakaoBookEntity body = kakaoTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, headers), KakaoBookEntity.class).getBody();
        return body.getDocuments().stream().map(d -> getBookFromKakao(d)).collect(Collectors.toList());
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(1000))
    public List<Book> callNaverApi(@NotEmpty String query, @Min(1) @Max(1000) int start, @Min(10) @Max(100) int display) throws Exception {
        String url = naverUrl + "?query=" + query + "&start=" + start + "&display=" + display;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", naverClientId);
        headers.add("X-Naver-Client-Secret", naverClientSecret);
        NaverBookEntity body = naverTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, headers), NaverBookEntity.class).getBody();
        return body.getItems().stream().map(i -> getBookFromNaver(i)).collect(Collectors.toList());
    }

    private Book getBookFromNaver(Item item) {
        Book book = new Book();
        book.setTitle(item.getTitle());
        book.setContents(item.getDescription());
        book.setPublisher(item.getPublisher());
        book.setPrice(item.getPrice().length() == 0 ? 0 : Integer.parseInt(item.getPrice()));
        book.setSalePrice(item.getDiscount().length() == 0 ? 0 : Integer.parseInt(item.getDiscount()));
        book.setUrl(item.getLink());
        book.setAuthors(Arrays.asList(item.getAuthor()));
        return book;
    }

    private Book getBookFromKakao(Document doc) {
        Book book = new Book();
        BeanUtils.copyProperties(doc, book);
        return book;
    }

}
