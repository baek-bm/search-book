package com.baek.app.searchbook.dto;

import lombok.Data;

import java.util.List;

@Data
public class Book {
    private String title;
    private String contents;
    private String publisher;
    private Integer price;
    private Integer salePrice;
    private String url;
    private List<String> authors;
}
