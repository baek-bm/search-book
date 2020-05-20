package com.baek.app.searchbook.repository;

import com.baek.app.searchbook.repository.entity.SearchCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SearchCountRepository extends JpaRepository<SearchCountEntity, Long> {
    List<SearchCountEntity> findTop10ByOrderByQueryCountDesc();

    @Modifying
    @Transactional
    @Query(value = "UPDATE SearchCountEntity t set t.queryCount = t.queryCount + :inc_count where t.query = :query")
    int increaseQueryCountByQuery(@Param("query") String query, @Param("inc_count") long incCount);

    @Modifying
    @Transactional
    @Query(value = "insert into search_count (query, queryCount) values (:query, :inc_count)", nativeQuery = true)
    int insertSearchCount(@Param("query") String query, @Param("inc_count") long incCount);
}
