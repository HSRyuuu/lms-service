package com.zerobase.fastlms.main.controller;


import com.zerobase.fastlms.banner.entity.Banner;
import com.zerobase.fastlms.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final BannerService bannerService;

    @RequestMapping("/")
    public String index(Model model) {
        List<Banner> banners = bannerService.frontList();

        if(!CollectionUtils.isEmpty(banners)){
            model.addAttribute("list",  banners);
        }

        return "index";
    }

    @RequestMapping("/error/denied")
    public String errorDenied() {
        return "error/denied";
    }
}
