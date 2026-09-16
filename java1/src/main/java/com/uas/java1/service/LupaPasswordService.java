package com.uas.java1.service;

import com.uas.java1.dto.LupaPasswordRequestDto;
import com.uas.java1.dto.OtpResetPasswordDto;
import com.uas.java1.dto.ResetPasswordDto;

public interface LupaPasswordService {
    void requestPasswordReset(LupaPasswordRequestDto request);
    void resetPassword(ResetPasswordDto request);
    void validasiOtpDanResetPassword(OtpResetPasswordDto dto);
}
 