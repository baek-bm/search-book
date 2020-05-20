package com.baek.app.searchbook.service;

import com.baek.app.searchbook.client.BookSearchOpenApiClient;
import com.baek.app.searchbook.client.KafkaClient;
import com.baek.app.searchbook.dto.Book;
import com.baek.app.searchbook.dto.SearchHistory;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class BookSearchService {
    private final BookSearchOpenApiClient openApiClient;

    private final KafkaClient kafkaClient;


    @HystrixCommand(fallbackMethod = "fallBack")
    public List<Book> searchBook(@NotEmpty String query, @Min(1) @Max(50) int page, @Min(1) @Max(50) int size, @Email String email) throws Exception {
        asyncAddSearchHistory(query, page, size, email);
        return openApiClient.callKakaoApi(query, page, size);
    }

    private List<Book> fallBack(@NotEmpty String query, @Min(1) @Max(50) int page, @Min(1) @Max(50) int size, @Email String email) throws Exception {
        int start = ((page - 1) * size) + 1;
        start = start > 1000 ? 1000 : start;
        return openApiClient.callNaverApi(query, start, size);
    }

    @Async
    public void asyncAddSearchHistory(String query, int page, int size, String email) {
        try {
            SearchHistory e = new SearchHistory();
            e.setEmail(email);
            e.setQuery(query);
            e.setPage(page);
            e.setSize(size);
            e.setSearchTimestamp(LocalDateTime.now());
            kafkaClient.produce("search_history", e);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
