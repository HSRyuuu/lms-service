package com.zerobase.lms.member.model;

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
