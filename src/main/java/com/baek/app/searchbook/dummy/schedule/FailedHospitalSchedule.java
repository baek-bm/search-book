package com.baek.app.searchbook.dummy.schedule;

import com.baek.app.searchbook.client.KafkaClient;
import com.baek.app.searchbook.dummy.dto.SearchHistoryFail;
import com.baek.app.searchbook.service.SearchCountService;
import com.baek.app.searchbook.service.SearchHistoryService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FailedHospitalSchedule {
    private final KafkaClient kafkaClient;
    private final SearchHistoryService searchHistoryService;
    private final SearchCountService searchCountService;

    /**
     * 실패한 카운트 증가 요청 처리
     * 5회 이상 실패 시 알림과 함께 수동처리 프로세스 진행. 수동을 위한 프로세스는 여기서는 구현하지 않는다.
     */
    @Scheduled(fixedDelayString = "1000")
    public void increaseCountFailedHospital() {
        List<SearchHistoryFail> l = kafkaClient.consume("search_count_increment_failed_topic", "test-group-1", 10, SearchHistoryFail.class);
        for (SearchHistoryFail failed : l) {
            if (failed.getFailedCount() > 5) {
                log.error("** FAILED TOO MANY TIMES *** ");
                log.error("\tQuery : " + failed.getQuery());
                log.error("\tFailed Count : " + failed.getFailedCount());
                log.error("\tIncrease Size : " + failed.getList().size());
                continue;
            }

            try {
                searchCountService.increaseSearchCount(failed.getQuery(), failed.getList());
            } catch (Exception e) {
                failed.increaseFailedCount();
                log.error("Failed increase count... is going to failed hospital. " + failed.getQuery() + ", " + failed.getFailedCount() + ", " + failed.getList().size(), e);
                kafkaClient.produce("search_count_increment_failed_topic", new Gson().toJson(failed));
            }
        }
    }

    /**
     * 실패한 검색 히스토리 추가 처리
     * 5회 이상 실패 시 알림과 함께 수동처리 프로세스 진행. 수동을 위한 프로세스는 여기서는 구현하지 않는다.
     */
    @Scheduled(fixedDelayString = "1000")
    public void insertSearchHistoryFailedHospital() {
        List<SearchHistoryFail> l = kafkaClient.consume("insert_search_history_failed_topic", "test-group-1", 10, SearchHistoryFail.class);
        for (SearchHistoryFail failed : l) {
            if (failed.getFailedCount() > 5) {
                log.error("** FAILED TOO MANY TIMES *** ");
                log.error("\tList : " + failed.getList());
                log.error("\tFailed Count : " + failed.getFailedCount());
                continue;
            }

            try {
                searchHistoryService.saveAll(failed.getList());
            } catch (Exception e) {
                failed.increaseFailedCount();
                log.error("Failed insert searchHistory... is going to failed hospital. " + failed.getFailedCount() + ", " + failed.getList().size(), e);
                kafkaClient.produce("search_count_increment_failed_topic", new Gson().toJson(failed));
            }
        }
    }
}
