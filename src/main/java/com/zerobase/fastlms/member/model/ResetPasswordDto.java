package com.zerobase.fastlms.member.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ResetPasswordDto {
    private String userId;
    private String userName;

    private String id;
    private String password;
}
