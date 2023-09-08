package com.zerobase.lms.web.controller.member;

import com.zerobase.lms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberXXXController {

    private final MemberService memberService;

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
