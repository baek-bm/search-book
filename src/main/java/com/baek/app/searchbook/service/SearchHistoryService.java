package com.baek.app.searchbook.service;

import com.baek.app.searchbook.dto.SearchHistory;
import com.baek.app.searchbook.repository.SearchHistoryRepository;
import com.baek.app.searchbook.repository.entity.SearchHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class SearchHistoryService {
    private final SearchHistoryRepository searchHistoryRepository;

    public List<SearchHistory> getSearchHistories(@Email String email, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, "historyId");
        Page<SearchHistoryEntity> byEmailEqualsOrderByHistoryIdDesc = searchHistoryRepository.findByEmailEqualsOrderByHistoryIdDesc(email, pageable);
        return byEmailEqualsOrderByHistoryIdDesc.stream()
                .map(e -> {
                    SearchHistory h = new SearchHistory();
                    BeanUtils.copyProperties(e, h);
                    return h;
                }).collect(Collectors.toList());

    }

    public void saveAll(List<SearchHistory> l) {
        searchHistoryRepository.saveAll(l.stream().map(dto -> dto.toEntity()).collect(Collectors.toList()));
    }
}
