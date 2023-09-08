package com.zerobase.lms.web;

import com.zerobase.lms.model.member.MemberInput;
import com.zerobase.lms.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     * @return
     */
    @GetMapping("/member/register")
    public String register() {

        return "member/register";
    }

    @PostMapping("/member/register")
    public String registerSubmit(Model model, MemberInput parameter) {
        boolean result = memberService.register(parameter);

        model.addAttribute("result", result);


        return "member/register_complete";
    }

    @GetMapping("/member/email-auth")
    public String emailAuth(Model model, @RequestParam("id") String uuid) {
        log.info("Email-auth [UUID : {}]", uuid);

        boolean result = memberService.emailAuth(uuid);
        model.addAttribute("result", result);

        return "member/email_auth";
    }

    @RequestMapping ("/member/login")
    public String login() {
        return "member/login";
    }



//    @GetMapping("/member/info")
//    public String memberInfo(Model model, Principal principal) {
//
//        String userId = principal.getName();
//        MemberDto detail = memberService.detail(userId);
//
//        model.addAttribute("detail", detail);
//
//        return "member/info";
//    }

}
