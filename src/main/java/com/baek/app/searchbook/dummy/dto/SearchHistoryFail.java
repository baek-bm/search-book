package com.baek.app.searchbook.dummy.dto;

import com.baek.app.searchbook.dto.SearchHistory;
import lombok.Data;

import java.util.List;

@Data
public class SearchHistoryFail {
    private String query;
    private List<SearchHistory> list;
    private int failedCount = 0;

    public int increaseFailedCount() {
        return ++this.failedCount;
    }
}
