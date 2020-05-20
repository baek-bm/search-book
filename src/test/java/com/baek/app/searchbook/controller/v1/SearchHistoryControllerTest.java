package com.baek.app.searchbook.controller.v1;

import com.baek.app.searchbook.dto.SearchHistory;
import com.baek.app.searchbook.repository.Role;
import com.baek.app.searchbook.repository.entity.MemberEntity;
import com.baek.app.searchbook.service.MemberService;
import com.baek.app.searchbook.service.SearchHistoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SearchHistoryControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @MockBean
    SearchHistoryService searchHistoryService;

    String email = "qkfl4@naver.com";

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    public void 내_분석요청_히스토리() throws Exception {
        int page = 1;
        int size = 10;
        String q = "스프링 부트";

        SearchHistory e = new SearchHistory().setQuery(q).setPage(page).setSize(size).setEmail(email).setHistoryId(1);
        given(searchHistoryService.getSearchHistories(email, page, size)).willReturn(Arrays.asList(e));

        MvcResult ret = this.mvc.perform(get("/api/v1/history/my?page=1&size=10")
                .with(user(new MemberService.UserCustom(getMemberEntity(email), Arrays.asList(new SimpleGrantedAuthority(Role.ADMIN.getValue()))))))
                .andExpect(content().string("[{\"historyId\":1,\"email\":\"" + email + "\",\"query\":\"" + q + "\",\"page\":" + page + ",\"size\":" + size + ",\"searchTimestamp\":null}]"))
                .andExpect(status().isOk())
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