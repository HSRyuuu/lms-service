package com.zerobase.fastlms.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginHistoryDto {
    private Long id;
    private String userId;
    private LocalDateTime loginDt;
    private String remoteAddr;
    private String userAgent;

}
