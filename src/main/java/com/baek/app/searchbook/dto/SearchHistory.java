package com.baek.app.searchbook.dto;

import com.baek.app.searchbook.repository.entity.SearchHistoryEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class SearchHistory {
    private long historyId;
    private String email;
    private String query;
    private int page;
    private int size;
    private LocalDateTime searchTimestamp;

    public SearchHistoryEntity toEntity() {
        SearchHistoryEntity e = new SearchHistoryEntity();
        BeanUtils.copyProperties(this, e);
        return e;
    }
}
