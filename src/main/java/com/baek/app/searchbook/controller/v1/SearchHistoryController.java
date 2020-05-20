package com.baek.app.searchbook.controller.v1;

import com.baek.app.searchbook.dto.SearchCount;
import com.baek.app.searchbook.dto.SearchHistory;
import com.baek.app.searchbook.service.MemberService;
import com.baek.app.searchbook.service.SearchCountService;
import com.baek.app.searchbook.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/history", produces = "application/json; charset=utf8")
public class SearchHistoryController {
    private final SearchHistoryService searchHistoryService;
    private final SearchCountService searchCountService;

    @GetMapping("/my")
    public List<SearchHistory> searchBook(@RequestParam(name = "page", defaultValue = "1") @Min(1) @Max(50) int page,
                                          @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(50) int size,
                                          Authentication authentication
    ) throws AuthenticationException {
        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof MemberService.UserCustom) {
            List<SearchHistory> searchHistories = searchHistoryService.getSearchHistories(((MemberService.UserCustom) authentication.getPrincipal()).getEmail(), page, size);
            return searchHistories;
        } else {
            throw new AuthenticationException("login first.");
        }
    }

    @GetMapping("/top10")
    public List<SearchCount> getTop10SearchQuery() {
        return searchCountService.getTop10SearchQuery();
    }
}