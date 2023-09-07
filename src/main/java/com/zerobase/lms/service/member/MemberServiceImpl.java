package com.zerobase.lms.service.member;

import com.zerobase.lms.entity.member.Member;
import com.zerobase.lms.entity.member.MemberRepository;
import com.zerobase.lms.model.member.MemberInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    @Override
    public boolean register(MemberInput parameter) {
        Optional<Member> findMember = memberRepository.findById(parameter.getUserId());
        if(findMember.isPresent()){
            return false;
        }
        Member member = Member.builder()
                .userId(parameter.getUserId())
                .userName(parameter.getUserName())
                .phone(parameter.getPhone())
                .password(parameter.getPassword())
                .regDt(LocalDateTime.now())
                .build();

        memberRepository.save(member);

        return true;
    }
}















