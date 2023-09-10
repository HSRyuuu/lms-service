package com.zerobase.fastlms.admin.controller;

import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.admin.model.MemberInput;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.admin.loginhistory.LoginHistory;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping("/admin/member/list.do")
    public String list(Model model, MemberParam parameter) {
        parameter.init();

        List<MemberDto> memberList = memberService.list(parameter);

        long totalCount = 0;
        if (memberList != null && memberList.size() > 0) {
            totalCount = memberList.get(0).getTotalCount();
        }

        PageUtil pageUtil = new PageUtil(totalCount, parameter.getPageSize(), parameter.getPageIndex(), parameter.getQueryString());

        model.addAttribute("list", memberList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pager", pageUtil.pager());

        return "admin/member/list";
    }

    @GetMapping("/admin/member/detail.do")
    public String detail(Model model, MemberParam parameter) {
        parameter.init();

        MemberDto member = memberService.detail(parameter.getUserId());
        model.addAttribute("member", member);

        List<LoginHistory> historyList = memberService.loginHistoryList(parameter.getUserId());
        model.addAttribute("historyList", historyList);

        return "admin/member/detail";
    }

    @PostMapping("/admin/member/status.do")
    public String status(Model model, MemberInput parameter) {

        memberService.updateStatus(parameter.getUserId(), parameter.getUserStatus());

        return "redirect:/admin/member/detail.do?userId=" + parameter.getUserId();
    }

    @PostMapping("/admin/member/password.do")
    public String password(Model model, MemberInput parameter) {


        boolean result = memberService.updatePassword(parameter.getUserId(), parameter.getPassword());

        return "redirect:/admin/member/detail.do?userId=" + parameter.getUserId();
    }

    @GetMapping("/admin/member/history.do")
    public String loginHistory(@RequestParam String userId, Model model){
        List<LoginHistory> list = memberService.loginHistoryList(userId);

        model.addAttribute("list", list);

        return "admin/member/history";
    }



}
