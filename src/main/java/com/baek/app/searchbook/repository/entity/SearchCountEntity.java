package com.baek.app.searchbook.repository.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "search_count", indexes = {@Index(columnList = "queryCount"), @Index(columnList = "query")})
public class SearchCountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long countId;

    @Column(length = 50, nullable = false, unique = true)
    private String query;

    @Column(nullable = false)
    private long queryCount;
}