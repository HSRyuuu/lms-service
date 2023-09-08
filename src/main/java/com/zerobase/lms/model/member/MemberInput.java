package com.zerobase.lms.model.member;

import com.zerobase.lms.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@Builder
public class MemberInput {
    private String userId;
    private String userName;
    private String phone;
    private String password;

    private String newPassword;

    private String zipcode;
    private String addr;
    private String addrDetail;

}