package com.zerobase.lms.web;

import com.zerobase.lms.model.member.MemberInput;
import com.zerobase.lms.model.member.ResetPasswordDto;
import com.zerobase.lms.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


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

    @GetMapping("/member/find-password")
    public String findPassword() {
        return "member/find_password";
    }

    @PostMapping("/member/find-password")
    public String findPasswordSubmit(Model model, ResetPasswordDto parameter) {
        boolean result = false;
        try{
            result = memberService.sendResetPassword(parameter);
        }catch (Exception e){
            e.printStackTrace();
        }
        model.addAttribute("result", result);

        return "member/find_password_result";
    }

    @GetMapping("/member/reset/password")
    public String resetPassword(Model model, HttpServletRequest request) {
        boolean result = memberService.checkResetPassword(request.getParameter("id"));
        model.addAttribute("result", result);

        return "member/reset_password";
    }
    @PostMapping("/member/reset/password")
    public String resetPasswordSubmit(@RequestParam String id,
                                      Model model,
                                      ResetPasswordDto parameter){
        boolean result = false;
        try{
            result = memberService.resetPassword(id, parameter.getPassword());
        }catch(Exception e){
            e.printStackTrace();
        }
        model.addAttribute("result", result);

        return "member/reset_password_result";

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
