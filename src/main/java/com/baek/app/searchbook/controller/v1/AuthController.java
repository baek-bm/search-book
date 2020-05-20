package com.baek.app.searchbook.controller.v1;

import com.baek.app.searchbook.dto.Member;
import com.baek.app.searchbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth", produces = "application/json; charset=utf8")
public class AuthController {
    private final MemberService memberService;

    @PostMapping("/register-do")
    public ResponseEntity<Map<String, String>> registerDo(@Valid Member member) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        member.setCreatedDate(now);
        member.setModifiedDate(now);
        memberService.joinUser(member);
        return new ResponseEntity<>(new HashMap<String, String>(){{
            put("status", "ok");
        }}, HttpStatus.OK);
    }

    @GetMapping("/login-success")
    public ResponseEntity<Map<String, String>> loginSuccess() throws Exception {
        return new ResponseEntity<>(new HashMap<String, String>(){{
            put("status", "ok");
        }}, HttpStatus.OK);
    }
}