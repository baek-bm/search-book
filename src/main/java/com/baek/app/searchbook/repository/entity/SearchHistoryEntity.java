package com.baek.app.searchbook.repository.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "search_history", indexes = {@Index(columnList = "email")})
public class SearchHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long historyId;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String query;

    @Column(nullable = false)
    private int page;

    @Column(nullable = false)
    private int size;

    @Column(nullable = false)
    private LocalDateTime searchTimestamp;
}