package com.zerobase.lms.web.controller.admin;


import com.zerobase.lms.admin.service.MemberAdminService;
import com.zerobase.lms.member.entity.Member;
import com.zerobase.lms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberAdminService memberAdminService;

    @GetMapping("/admin/member/list.do")
    public String list(Model model) {
        List<Member> memberList = memberAdminService.list();
        model.addAttribute("list", memberList);
        return "admin/member/list";
    }

}
