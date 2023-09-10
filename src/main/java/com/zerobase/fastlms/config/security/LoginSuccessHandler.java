package com.zerobase.fastlms.config.security;

import com.zerobase.fastlms.admin.loginhistory.LoginHistory;
import com.zerobase.fastlms.admin.loginhistory.LoginHistoryRepository;

import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String name = authentication.getName();
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        log.info("[ login ] member: {}, remoteAddr: {}, userAgent: {}", name, remoteAddr, userAgent);

        this.updateMemberLoginDt(name);
        this.saveHistory(name, remoteAddr, userAgent);

        response.sendRedirect("/");
    }

    private void updateMemberLoginDt(String name){
        Member member = memberRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 사용자 입니다."));
        member.setLastLoginDt(LocalDateTime.now());
        memberRepository.save(member);
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
