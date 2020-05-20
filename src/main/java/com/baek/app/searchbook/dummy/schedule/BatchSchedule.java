package com.baek.app.searchbook.dummy.schedule;

import com.baek.app.searchbook.client.KafkaClient;
import com.baek.app.searchbook.dto.SearchHistory;
import com.baek.app.searchbook.dummy.dto.SearchHistoryFail;
import com.baek.app.searchbook.service.SearchCountService;
import com.baek.app.searchbook.service.SearchHistoryService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class BatchSchedule {
    private final KafkaClient kafkaClient;
    private final SearchHistoryService searchHistoryService;
    private final SearchCountService searchCountService;

    /**
     * 카프카 등 외부 서비스등에 추가된 이벤트를 받아 DB 나 기타 저장소에 저장하는 작업.
     * 실제 구현에서는 본 프로젝트가 아니라 따로 배치 프로젝트를 생성해서 동작시키게 합니다.
     */
    @Scheduled(fixedDelayString = "1000")
    public void dummyKafkaConsumer() {
        // 실제 이벤트 버스를 활용하지 않았기 때문에 한번 가져와서 재활용합니다.
        List<SearchHistory> l = kafkaClient.consume("search_history", "test-group-1", 10, SearchHistory.class);

        // 실제 하단의 두개의 메소드는 각각의 컨슈머로 구현하여, 확장합니다.
        insertSearchHistoryBatch(l);
        increaseSearchQueryCountBatch(l);
    }

    /**
     * 검색 히스토리를 영구 저장소에 저장합니다.
     *
     * @param l
     */
    private void insertSearchHistoryBatch(List<SearchHistory> l) {
        try {
            searchHistoryService.saveAll(l);
        } catch (Exception e) {
            log.error("Failed insert history... is going to failed hospital. " + l, e);
            kafkaClient.produce("insert_search_history_failed_topic", new Gson().toJson(buildSearchHistoryFail("all", l)));
        }
    }


    /**
     * 검색 쿼리에 따라 Top을 뽑기 위해 카운트 증가를 실행합니다.
     *
     * @param l
     */
    private void increaseSearchQueryCountBatch(List<SearchHistory> l) {
        Map<String, List<SearchHistory>> group = l.stream().collect(Collectors.groupingBy(SearchHistory::getQuery));
        for (String query : group.keySet()) {
            try {
                searchCountService.increaseSearchCount(query, group.get(query));
            } catch (Exception e) {
                log.error("Failed increase count... is going to failed hospital. " + query + ", " + group.get(query).size(), e);
                kafkaClient.produce("search_count_increment_failed_topic", new Gson().toJson(buildSearchHistoryFail(query, group.get(query))));
            }
        }
    }

    private SearchHistoryFail buildSearchHistoryFail(String query, List<SearchHistory> list) {
        SearchHistoryFail fail = new SearchHistoryFail();
        fail.setQuery(query);
        fail.setList(list);
        fail.increaseFailedCount();
        return fail;
    }
}
