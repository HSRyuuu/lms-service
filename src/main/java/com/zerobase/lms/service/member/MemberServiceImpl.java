package com.zerobase.lms.service.member;

import com.zerobase.lms.entity.loginhistory.LoginHistoryRepository;
import com.zerobase.lms.entity.member.Member;
import com.zerobase.lms.entity.member.MemberRepository;
import com.zerobase.lms.exception.MemberNotEmailAuthException;
import com.zerobase.lms.model.member.MemberInput;
import com.zerobase.lms.service.mail.MailComponents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;
    private final LoginHistoryRepository loginHistoryRepository;

    /**
     * 회원 가입
     * @param parameter : 회원 가입 입력 값
     * @return false : 이미 존재하는 이메일인 경우
     */
    @Override
    public boolean register(MemberInput parameter) {
        Optional<Member> findMember = memberRepository.findById(parameter.getUserId());
        if (findMember.isPresent()) {
            return false;
        }

        String uuid = UUID.randomUUID().toString();

        //비밀번호 암호화
        String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());
        parameter.setPassword(encPassword);

        //parameter를 Member 엔티티로 변환 후 이메일 인증 키 설정 후 저장
        Member member = Member.fromMemberInput(parameter);
        member.setEmailAuthKey(uuid);
        memberRepository.save(member);

        //해당 이메일로 이메일 인증을 위한 메일 발송
        this.sendRegisterAuthMail(parameter.getUserId(), uuid);

        return true;
    }
    /**
     * 해당 email, 제목, 내용으로 이메일 발송
     */
    private void sendRegisterAuthMail(String userId, String uuid) {
        String email = userId;
        String subject = "fastlms 사이트 가입을 축하드립니다. ";
        String text = new StringBuilder()
                .append("<p>fastlms 사이트 가입을 축하드립니다. </p>")
                .append("<p>아래 링크를 클릭하셔서 가입을 완료 하세요. </p>")
                .append("<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + uuid + "'>이메일 인증하기</a></div>")
                .toString();
        mailComponents.sendMail(email, subject, text);
    }

    @Override
    public boolean emailAuth(String uuid) {
        Optional<Member> findMember = memberRepository.findByEmailAuthKey(uuid);
        if (!findMember.isPresent()) {
            log.info("Email Auth by uuid - member not found");
            return false;
        }

        Member member = findMember.get();
        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);
        log.info("Email Auth by uuid - complete");
        return true;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));

        if(!member.isEmailAuthYn()){
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인을 해 주세요.");
        }
        log.info("로그인 성공");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(member.getUserId(), member.getPassword(), grantedAuthorities);
    }

}















