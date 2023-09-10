package com.zerobase.fastlms.banner.controller;

import com.zerobase.fastlms.banner.entity.Banner;
import com.zerobase.fastlms.banner.model.BannerInput;
import com.zerobase.fastlms.banner.service.BannerService;
import com.zerobase.fastlms.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @GetMapping("/admin/banner/list.do")
    public String list(Model model) {
        List<Banner> list = bannerService.list();
        model.addAttribute("list", list);
        return "admin/banner/list";
    }


    @GetMapping("/admin/banner/add.do")
    public String addBanner() {
        return "admin/banner/add";
    }

    @PostMapping("/admin/banner/add.do")
    public String addBannerSubmit(Model model, MultipartFile file,
                                  BannerInput parameter) {
        saveFile(file, parameter);
        parameter.setCreateDt(LocalDateTime.now());
        bannerService.add(parameter);
        return "redirect:/admin/banner/list.do";
    }

    @GetMapping("/admin/banner/edit.do")
    public String editBanner(Model model, BannerInput parameter){
        Banner find = bannerService.findById(parameter.getId());
        model.addAttribute("detail", find);

        return "admin/banner/edit";
    }

    @PostMapping("/admin/banner/edit.do")
    public String editBannerSubmit(BannerInput parameter, MultipartFile file){
        if(!file.isEmpty()){
            saveFile(file, parameter);
        }else{
            Banner find = bannerService.findById(parameter.getId());
            parameter.setFileName(find.getFileName());
            parameter.setUrlFileName(find.getUrlFileName());
        }
        log.info("edit : " + parameter);
        bannerService.update(parameter);

        return "redirect:/admin/banner/list.do";
    }


    @PostMapping("/admin/banner/delete.do")
    public String delete(BannerInput bannerInput){
        bannerService.delete(bannerInput.getId());
        return "redirect:/admin/banner/list.do";
    }

    private void saveFile(MultipartFile file, BannerInput parameter){
        String saveFilename = "";
        String urlFilename = "";

        if (file != null) {
            String originalFilename = file.getOriginalFilename();

            String baseLocalPath = "C:\\dev_zerobase\\fastlms\\files\\banner";
            String baseUrlPath = "/files/banner";

            String[] arrFilename = FileUtil.getNewSaveFile(baseLocalPath, baseUrlPath, originalFilename);

            saveFilename = arrFilename[0];
            urlFilename = arrFilename[1];

            try {
                File newFile = new File(saveFilename);
                FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
            } catch (IOException e) {
                log.info("############################ - 1");
                log.info(e.getMessage());
            }
        }
        parameter.setFileName(saveFilename);
        parameter.setUrlFileName(urlFilename);
    }

}
