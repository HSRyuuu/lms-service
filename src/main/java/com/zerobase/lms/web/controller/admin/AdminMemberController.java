package com.zerobase.lms.web.controller.admin;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class AdminMemberController {

    @GetMapping("/admin/member/list.do")
    public String list() {

        return "admin/member/list";
    }

}
