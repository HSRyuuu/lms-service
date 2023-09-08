package com.zerobase.lms.config.security;

import com.zerobase.lms.loginhistory.LoginHistory;
import com.zerobase.lms.loginhistory.LoginHistoryRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        WebAuthenticationDetails web = (WebAuthenticationDetails) authentication.getDetails();
        String name = authentication.getName();
        String remoteAddr = web.getRemoteAddress();
        String userAgent = request.getHeader("User-Agent");

        this.saveHistory(name, remoteAddr, userAgent);

        response.sendRedirect("/");
    }

    private void saveHistory(String name, String remoteAddr, String userAgent) {
        loginHistoryRepository.save(LoginHistory.builder()
                .userId(name)
                .loginDt(LocalDateTime.now())
                .remoteAddr(remoteAddr)
                .userAgent(userAgent)
                .build());
    }


}
