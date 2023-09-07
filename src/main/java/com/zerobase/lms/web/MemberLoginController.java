package com.zerobase.lms.web;

import com.zerobase.lms.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberLoginController {

    private final MemberService memberService;

    @RequestMapping("/member/login")
    public String loginForm() {

        return "member/login";
    }
//    @PostMapping("/member/login")
//    public String login(@RequestHeader("User-Agent") String userAgent) {
//        log.info(userAgent);
//        return "index";
//    }
}
