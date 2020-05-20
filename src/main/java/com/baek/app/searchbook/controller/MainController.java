package com.baek.app.searchbook.controller;

import com.baek.app.searchbook.dto.Member;
import com.baek.app.searchbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {
    private final MemberService memberService;

    // 메인 페이지
    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index");
        return modelAndView;
    }

    @GetMapping("/auth/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("/auth/login");
        return modelAndView;
    }

    @GetMapping("/auth/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("/auth/register");
        return modelAndView;
    }

    @PostMapping("/auth/register-do")
    public ModelAndView registerDo(@Valid Member member) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        member.setCreatedDate(now);
        member.setModifiedDate(now);
        memberService.joinUser(member);
        ModelAndView modelAndView = new ModelAndView("redirect:/auth/login");
        return modelAndView;
    }
}