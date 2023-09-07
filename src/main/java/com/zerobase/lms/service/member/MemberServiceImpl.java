package com.zerobase.lms.service.member;

import com.zerobase.lms.entity.member.Member;
import com.zerobase.lms.entity.member.MemberRepository;
import com.zerobase.lms.model.member.MemberInput;
import com.zerobase.lms.service.mail.MailComponents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;

    @Override
    public boolean register(MemberInput parameter) {
        Optional<Member> findMember = memberRepository.findById(parameter.getUserId());
        if (findMember.isPresent()) {
            return false;
        }

        String uuid = UUID.randomUUID().toString();

        Member member = Member.fromMemberInput(parameter);
        member.setEmailAuthKey(uuid);
        memberRepository.save(member);

        this.sendRegisterAuthMail(parameter.getUserId(), uuid);

        return true;
    }

    /**
     * 해당 email, 제목, 내용으로 이메일 전송
     */
    private void sendRegisterAuthMail(String userId, String uuid) {
        String email = userId;
        String subject = "fastlms 사이트 가입을 축하드립니다. ";
        String text = new StringBuilder()
                .append("<p>fastlms 사이트 가입을 축하드립니다. </p>")
                .append("<p>아래 링크를 클릭하셔서 가입을 완료 하세요. </p>")
                .append("<div><a href='http://localhost:8080/member/email-auth?id=" + uuid + "'>이메일 인증하기</a></div>")
                .toString();
        mailComponents.sendMail(email, subject, text);
    }

}















