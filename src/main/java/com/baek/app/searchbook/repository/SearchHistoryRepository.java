package com.baek.app.searchbook.repository;

import com.baek.app.searchbook.repository.entity.SearchHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistoryEntity, Long> {
    Page<SearchHistoryEntity> findByEmailEqualsOrderByHistoryIdDesc(String email, Pageable pageable);
}
