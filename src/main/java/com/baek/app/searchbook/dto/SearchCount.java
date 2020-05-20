package com.baek.app.searchbook.dto;

import com.baek.app.searchbook.repository.entity.SearchCountEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

@Data
@Accessors(chain = true)
public class SearchCount {
    private String query;
    private long queryCount;

    public static SearchCount convertToDto(SearchCountEntity entry) {
        SearchCount target = new SearchCount();
        BeanUtils.copyProperties(entry, target);
        return target;
    }
}