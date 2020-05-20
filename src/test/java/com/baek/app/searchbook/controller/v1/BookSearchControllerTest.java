package com.baek.app.searchbook.controller.v1;

import com.baek.app.searchbook.client.BookSearchOpenApiClient;
import com.baek.app.searchbook.dto.Book;
import com.baek.app.searchbook.dto.SearchHistory;
import com.baek.app.searchbook.repository.Role;
import com.baek.app.searchbook.repository.SearchHistoryRepository;
import com.baek.app.searchbook.repository.entity.MemberEntity;
import com.baek.app.searchbook.repository.entity.SearchHistoryEntity;
import com.baek.app.searchbook.service.BookSearchService;
import com.baek.app.searchbook.service.MemberService;
import com.baek.app.searchbook.service.SearchHistoryService;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class BookSearchControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @MockBean
    SearchHistoryService searchHistoryService;

    @MockBean
    BookSearchOpenApiClient openApiClient;

    String email = "qkfl4@naver.com";

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    public void 사이즈를_최대치_이상으로_검색() throws Exception {
        String q = "스프링 부트";
        MvcResult ret = this.mvc.perform(get("/api/v1/search/book?query=" + q + "&size=51")
                .with(user(new MemberService.UserCustom(getMemberEntity(email), Arrays.asList(new SimpleGrantedAuthority(Role.ADMIN.getValue()))))))
                .andExpect(status().is5xxServerError())
                .andDo(print())
                .andReturn();
    }

    @Test
    public void 사이즈를_최소치_이하로_검색() throws Exception {
        String q = "스프링 부트";
        MvcResult ret = this.mvc.perform(get("/api/v1/search/book?query=" + q + "&size=0")
                .with(user(new MemberService.UserCustom(getMemberEntity(email), Arrays.asList(new SimpleGrantedAuthority(Role.ADMIN.getValue()))))))
                .andExpect(status().is5xxServerError())
                .andDo(print())
                .andReturn();
    }

    @Test
    public void 쿼리_없이_검색() throws Exception {
        MvcResult ret = this.mvc.perform(get("/api/v1/search/book")
                .with(user(new MemberService.UserCustom(getMemberEntity(email), Arrays.asList(new SimpleGrantedAuthority(Role.ADMIN.getValue()))))))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    public void 정상적_책_검색() throws Exception {
        int page = 1;
        int size = 10;
        String q = "스프링 부트";

        MvcResult ret = this.mvc.perform(get("/api/v1/search/book?query=" + q + "&size=" + size + "&page=" + page)
                .with(user(new MemberService.UserCustom(getMemberEntity(email), Arrays.asList(new SimpleGrantedAuthority(Role.ADMIN.getValue()))))))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        Assert.assertTrue(ret.getResponse().getContentAsString().contains(q));
    }

    @Test
    public void 서킷_브레이커_테스트() throws Exception {
        int page = 1;
        int size = 10;
        int start = ((page - 1) * size) + 1;
        start = start > 1000 ? 1000 : start;
        String q = "스프링 부트";

        Book book = new Book();
        book.setTitle(q);
        book.setContents(q + "desc");
        book.setPrice(2);
        book.setSalePrice(1);
        book.setPublisher("출판사");
        book.setAuthors(Arrays.asList("작가"));
        book.setUrl("www.naver.com");
        given(openApiClient.callKakaoApi(q, page, size)).willThrow(new Exception("test"));
        given(openApiClient.callNaverApi(q, start, size)).willReturn(Arrays.asList(book));

        MvcResult ret = this.mvc.perform(get("/api/v1/search/book?query=" + q + "&size=" + size + "&page=" + page)
                .with(user(new MemberService.UserCustom(getMemberEntity(email), Arrays.asList(new SimpleGrantedAuthority(Role.ADMIN.getValue()))))))
                .andExpect(status().isOk())
                .andExpect(content().string(new Gson().toJson(Arrays.asList(book))))
                .andDo(print())
                .andReturn();
        Assert.assertTrue(ret.getResponse().getContentAsString().contains(q));
    }

    private MemberEntity getMemberEntity(String email) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUserName("백병민");
        memberEntity.setPassword("1234");
        memberEntity.setEmail(email);
        return memberEntity;
    }

}