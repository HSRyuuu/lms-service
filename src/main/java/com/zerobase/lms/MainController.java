package com.zerobase.lms;


import com.zerobase.lms.service.mail.MailComponents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class MainController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
    
    
    
    @RequestMapping("/error/denied")
    public String errorDenied() {
        return "error/denied";
    }
    
    
    
}
