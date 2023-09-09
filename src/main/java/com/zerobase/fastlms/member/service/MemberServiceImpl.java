package com.zerobase.fastlms.member.service;

import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.admin.mapper.MemberMapper;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.loginhistory.LoginHistory;
import com.zerobase.fastlms.loginhistory.LoginHistoryRepository;
import com.zerobase.fastlms.mail.MailComponents;
import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.entity.MemberCode;
import com.zerobase.fastlms.member.exception.MemberNotEmailAuthException;
import com.zerobase.fastlms.member.exception.MemberStopUserException;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordDto;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final MailComponents mailComponents;

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
        member.setUserStatus(Member.MEMBER_STATUS_REQ);
        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "fastlms 사이트 가입을 축하드립니다. ";
        String text = new StringBuilder()
                .append("<p>fastlms 사이트 가입을 축하드립니다. </p>")
                .append("<p>아래 링크를 클릭하셔서 가입을 완료 하세요. </p>")
                .append("<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + uuid + "'>이메일 인증하기</a></div>")
                .toString();
        //메일 발송
        mailComponents.sendMail(email, subject, text);


        return true;
    }

    @Override
    public boolean emailAuth(String uuid) {
        Optional<Member> findMember = memberRepository.findByEmailAuthKey(uuid);
        if (!findMember.isPresent()) {
            log.info("Email Auth by uuid - member not found");
            return false;
        }

        Member member = findMember.get();
        if(member.isEmailAuthYn()){
            log.info("이미 활성화 된 유저 입니다.");
            return false;
        }
        member.setUserStatus(Member.MEMBER_STATUS_ING);
        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);
        log.info("Email Auth by uuid - complete");
        return true;
    }

    @Override
    public boolean sendResetPassword(ResetPasswordDto parameter) {

        Member member = memberRepository.findByUserIdAndUserName(parameter.getUserId(), parameter.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));

        String uuid = UUID.randomUUID().toString();

        member.setResetPasswordKey(uuid);
        member.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1));
        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "[fastlms] 비밀번호 초기화 메일 입니다. ";
        String text = "<p>fastlms 비밀번호 초기화 메일 입니다.<p>" +
                "<p>아래 링크를 클릭하셔서 비밀번호를 초기화 해주세요.</p>"+
                "<div><a target='_blank' href='http://localhost:8080/member/reset/password?id=" + uuid + "'> 비밀번호 초기화 링크 </a></div>";
        mailComponents.sendMail(email, subject, text);
        return true;
    }

    @Override
    public boolean resetPassword(String uuid, String password) {
        Member member = memberRepository.findByResetPasswordKey(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));
        if(member.getResetPasswordLimitDt() == null){
            throw new RuntimeException("ResetPasswordLimitDt -> 유효한 날짜가 아닙니다.");
        }
        if(member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("ResetPasswordLimitDt -> 유효한 날짜가 아닙니다.");
        }

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean checkResetPassword(String uuid) {
        Optional<Member> findMember = memberRepository.findByResetPasswordKey(uuid);
        if(!findMember.isPresent()){
            return false;
        }
        Member member = findMember.get();

        if(member.getResetPasswordLimitDt() == null){
            throw new RuntimeException("ResetPasswordLimitDt -> 유효한 날짜가 아닙니다.");
        }
        if(member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("ResetPasswordLimitDt -> 유효한 날짜가 아닙니다.");
        }

        return true;
    }

    @Override
    public List<MemberDto> list(MemberParam parameter) {
        long totalCount = memberMapper.selectListCount(parameter);
        List<MemberDto> list = memberMapper.selectList(parameter);

        if(!CollectionUtils.isEmpty(list)){
            int i = 0;
            for(MemberDto x : list){
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i++);
            }
        }
        return list;
    }

    @Override
    public MemberDto detail(String userId) {

        Optional<Member> optionalMember  = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return null;
        }

        Member member = optionalMember.get();

        return MemberDto.of(member);
    }

    @Override
    public boolean updateStatus(String userId, String userStatus) {

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        member.setUserStatus(userStatus);
        memberRepository.save(member);
        return true;
    }

    @Override
    public boolean updatePassword(String userId, String password) {

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        memberRepository.save(member);

        return true;

    }

    @Override
    public List<LoginHistory> loginHistoryList(String userId) {
        return loginHistoryRepository.findTop20ByUserIdOrderByIdDesc(userId);
    }


    @Override
    public ServiceResult updateMember(MemberInput parameter) {

        String userId = parameter.getUserId();

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        member.setPhone(parameter.getPhone());
        member.setZipcode(parameter.getZipcode());
        member.setAddr(parameter.getAddr());
        member.setAddrDetail(parameter.getAddrDetail());
        member.setUdtDt(LocalDateTime.now());
        memberRepository.save(member);

        return new ServiceResult();
    }

    @Override
    public ServiceResult updateMemberPassword(MemberInput parameter) {

        String userId = parameter.getUserId();

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if (!PasswordUtils.equals(parameter.getPassword(), member.getPassword())) {
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }

        String encPassword = PasswordUtils.encPassword(parameter.getNewPassword());
        member.setPassword(encPassword);
        memberRepository.save(member);

        return new ServiceResult(true);
    }

    @Override
    public ServiceResult withdraw(String userId, String password) {

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if (!PasswordUtils.equals(password, member.getPassword())) {
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }

        member.setUserName("삭제회원");
        member.setPhone("");
        member.setPassword("");
        member.setRegDt(null);
        member.setUdtDt(null);
        member.setEmailAuthYn(false);
        member.setEmailAuthDt(null);
        member.setEmailAuthKey("");
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        member.setUserStatus(MemberCode.MEMBER_STATUS_WITHDRAW);
        member.setZipcode("");
        member.setAddr("");
        member.setAddrDetail("");
        memberRepository.save(member);

        return new ServiceResult();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));

        if(Member.MEMBER_STATUS_REQ.equals(member.getUserStatus())){
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인을 해 주세요.");
        }
        if (Member.MEMBER_STATUS_STOP.equals(member.getUserStatus())){
            throw new MemberStopUserException("정지된 회원 입니다.");
        }

        log.info("로그인 성공 [ ID :{} ]", member.getUserId());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        //관리자인 경우
        if(member.isAdminYn()){
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new User(member.getUserId(), member.getPassword(), grantedAuthorities);
    }

}















