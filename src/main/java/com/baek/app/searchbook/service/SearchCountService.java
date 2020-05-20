package com.baek.app.searchbook.service;

import com.baek.app.searchbook.dto.SearchCount;
import com.baek.app.searchbook.dto.SearchHistory;
import com.baek.app.searchbook.repository.SearchCountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SearchCountService {
    private final SearchCountRepository searchCountRepository;

    public List<SearchCount> getTop10SearchQuery() {
        return searchCountRepository.findTop10ByOrderByQueryCountDesc().stream().map(SearchCount::convertToDto).collect(Collectors.toList());
    }

    public void increaseSearchCount(String query, List<SearchHistory> list) {
        if (searchCountRepository.increaseQueryCountByQuery(query, list.size()) == 0) {
            try {
                searchCountRepository.insertSearchCount(query, list.size());
            } catch (DataIntegrityViolationException e) {
                log.warn("Already exist in database. " + query, e);
                searchCountRepository.increaseQueryCountByQuery(query, list.size());
            } catch (Exception e) {
                throw e;
            }
        }
    }

}
