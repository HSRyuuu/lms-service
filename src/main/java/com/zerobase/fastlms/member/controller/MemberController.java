package com.zerobase.fastlms.member.controller;

import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.course.dto.TakeCourseDto;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.service.TakeCourseService;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordDto;
import com.zerobase.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TakeCourseService takeCourseService;

    /**
     * 회원가입
     */
    @GetMapping("/member/register")
    public String register() {

        return "member/register";
    }

    @PostMapping("/member/register")
    public String registerSubmit(Model model, MemberInput parameter) {
        log.info("회원가입 : {}", parameter);
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

    @GetMapping("/member/info")
    public String memberInfo(Model model, Principal principal) {

        String userId = principal.getName();
        MemberDto detail = memberService.detail(userId);

        model.addAttribute("detail", detail);

        return "member/info";
    }

    @PostMapping("/member/info")
    public String memberInfoSubmit(Model model
            , MemberInput parameter
            , Principal principal) {

        String userId = principal.getName();
        parameter.setUserId(userId);

        ServiceResult result = memberService.updateMember(parameter);
        if (!result.isResult()) {
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }
        return "redirect:/member/info";
    }

    @GetMapping("/member/password")
    public String memberPassword(Model model, Principal principal) {

        String userId = principal.getName();
        MemberDto detail = memberService.detail(userId);

        model.addAttribute("detail", detail);

        return "member/password";
    }

    @PostMapping("/member/password")
    public String memberPasswordSubmit(Model model
            , MemberInput parameter
            , Principal principal) {

        String userId = principal.getName();
        parameter.setUserId(userId);

        ServiceResult result = memberService.updateMemberPassword(parameter);
        if (!result.isResult()) {
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }

        return "redirect:/member/info";
    }


    @GetMapping("/member/takecourse")
    public String memberTakeCourse(Model model, Principal principal) {

        String userId = principal.getName();
        List<TakeCourseDto> list = takeCourseService.myCourse(userId);

        model.addAttribute("list", list);

        return "member/takecourse";
    }


    @GetMapping("/member/withdraw")
    public String memberWithdraw(Model model) {

        return "member/withdraw";
    }

    @PostMapping("/member/withdraw")
    public String memberWithdrawSubmit(Model model
            , MemberInput parameter
            , Principal principal) {

        String userId = principal.getName();

        ServiceResult result = memberService.withdraw(userId, parameter.getPassword());
        if (!result.isResult()) {
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }

        return "redirect:/member/logout";
    }


}
