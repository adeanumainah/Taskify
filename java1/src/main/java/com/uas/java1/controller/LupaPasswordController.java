package com.uas.java1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uas.java1.dto.LupaPasswordRequestDto;
import com.uas.java1.dto.ResetPasswordDto;
import com.uas.java1.dto.ResponUmum;
import com.uas.java1.dto.OtpResetPasswordDto;
import com.uas.java1.service.LupaPasswordService;

@RestController
@RequestMapping("/password")
public class LupaPasswordController {

    @Autowired
    private LupaPasswordService lupaPasswordService;

    @PostMapping("/lupa")
    public ResponseEntity<ResponUmum<String>> requestPasswordReset(@RequestBody LupaPasswordRequestDto request) {
        try {
            lupaPasswordService.requestPasswordReset(request);
            return ResponseEntity.ok(ResponUmum.<String>builder()
                    .berhasil(true)
                    .pesan("OTP Telah Dikirim Ke Email Anda")
                    .data(null)
                    .build());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ResponUmum.<String>builder()
                            .berhasil(false)
                            .pesan(ex.getReason())
                            .data(null)
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(ResponUmum.<String>builder()
                            .berhasil(false)
                            .pesan("Kesalahan Internal Server")
                            .data(null)
                            .build());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<ResponUmum<String>> resetPassword(@RequestBody ResetPasswordDto request) {
        try {
            lupaPasswordService.resetPassword(request);
            return ResponseEntity.ok(ResponUmum.<String>builder()
                    .berhasil(true)
                    .pesan("Reset Kata Sandi Telah Berhasil")
                    .data(null)
                    .build());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ResponUmum.<String>builder()
                            .berhasil(false)
                            .pesan(ex.getReason())
                            .data(null)
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(ResponUmum.<String>builder()
                            .berhasil(false)
                            .pesan("Kesalahan Internal Server")
                            .data(null)
                            .build());
        }
    }

    @PostMapping("/verifikasi-otp")
    public ResponseEntity<ResponUmum<String>> verifikasiOtpDanResetPassword(@RequestBody OtpResetPasswordDto dto) {
        try {
            lupaPasswordService.validasiOtpDanResetPassword(dto);
            return ResponseEntity.ok(ResponUmum.<String>builder()
                    .berhasil(true)
                    .pesan("Kata Sandi Berhasil Diubah Menggunakan OTP")
                    .data(null)
                    .build());
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ResponUmum.<String>builder()
                            .berhasil(false)
                            .pesan(ex.getReason())
                            .data(null)
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(ResponUmum.<String>builder()
                            .berhasil(false)
                            .pesan("Internal Server Error")
                            .data(null)
                            .build());
        }
    }
}
