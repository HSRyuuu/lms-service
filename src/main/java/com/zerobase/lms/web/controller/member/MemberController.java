package com.zerobase.lms.web.controller.member;

import com.zerobase.lms.member.model.MemberInput;
import com.zerobase.lms.member.model.ResetPasswordDto;
import com.zerobase.lms.member.service.MemberService;
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
     *
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

    /**
     * 이메일 인증
     */
    @GetMapping("/member/email-auth")
    public String emailAuth(Model model, @RequestParam("id") String uuid) {
        log.info("Email-auth [UUID : {}]", uuid);

        boolean result = memberService.emailAuth(uuid);
        model.addAttribute("result", result);

        return "member/email_auth";
    }

    /**
     * 로그인
     * SecurityConfig / LoginSuccessHandler / UserAuthenticationFailureHandler 에서 처리
     */
    @RequestMapping("/member/login")
    public String login() {
        return "member/login";
    }

    /**
     * 비밀번호 찾기 form
     */
    @GetMapping("/member/find-password")
    public String findPassword() {
        return "member/find_password";
    }

    /**
     * 비밀번호 찾기 처리
     * @param parameter : userId(이메일) , userName(이름)
     */
    @PostMapping("/member/find-password")
    public String findPasswordSubmit(Model model, ResetPasswordDto parameter) {
        boolean result = false;
        try {
            result = memberService.sendResetPassword(parameter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("result", result);

        return "member/find_password_result";
    }

    /**
     * 비밀번호 초기화 form
     * checkResetPassword() : resetPasswordKey 유효성 확인
     * result : true - 정상 동작 , false - 유효성 검사 실패(메일 기간 만료)
     */
    @GetMapping("/member/reset/password")
    public String resetPassword(Model model, HttpServletRequest request) {
        boolean result = memberService.checkResetPassword(request.getParameter("id"));
        model.addAttribute("result", result);

        return "member/reset_password";
    }

    /**
     * 비밀번호 초기화 처리
     *
     * @param id : 비밀번호 초기화 uuid
     * @param parameter : 교체할 비밀번호
     * @return
     */
    @PostMapping("/member/reset/password")
    public String resetPasswordSubmit(@RequestParam String id,
                                      Model model,
                                      ResetPasswordDto parameter) {
        boolean result = false;
        try {
            result = memberService.resetPassword(id, parameter.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("result", result);

        return "member/reset_password_result";
    }


}
