package com.uas.java1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpResetPasswordDto {
    private String username;
    private String otp;
    private String passwordBaru;
}
