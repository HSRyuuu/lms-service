package com.zerobase.lms.admin.controller;

import com.zerobase.lms.admin.dto.MemberDto;
import com.zerobase.lms.admin.model.MemberParam;
import com.zerobase.lms.admin.service.MemberAdminService;
import com.zerobase.lms.util.PageUtil;
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
    public String list(Model model, MemberParam parameter) {
        parameter.init();

        List<MemberDto> memberList = memberAdminService.list(parameter);

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

}
