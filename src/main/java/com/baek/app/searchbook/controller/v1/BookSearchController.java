package com.baek.app.searchbook.controller.v1;

import com.baek.app.searchbook.dto.Book;
import com.baek.app.searchbook.service.BookSearchService;
import com.baek.app.searchbook.service.MemberService;
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
@RequestMapping(value = "/api/v1/search", produces = "application/json; charset=utf8")
public class BookSearchController {
    private final BookSearchService bookSearchService;

    @GetMapping("/book")
    public List<Book> searchBook(@RequestParam(name = "query") String query,
                                 @RequestParam(name = "page", defaultValue = "1") @Min(1) @Max(50) int page,
                                 @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(50) int size,
                                 Authentication authentication
    ) throws Exception {
        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof MemberService.UserCustom) {
            return bookSearchService.searchBook(query, page, size, ((MemberService.UserCustom) authentication.getPrincipal()).getEmail());
        } else {
            throw new AuthenticationException("login first.");
        }
    }

}