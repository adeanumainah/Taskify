package com.uas.java1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.uas.java1.dto.LupaPasswordRequestDto;
import com.uas.java1.dto.OtpResetPasswordDto;
import com.uas.java1.dto.ResetPasswordDto;
import com.uas.java1.model.TokenResetPassword;
import com.uas.java1.model.User;
import com.uas.java1.repository.TokenResetPasswordRepository;
import com.uas.java1.repository.UserRepository;
import com.uas.java1.util.OtpUtil;
import com.uas.java1.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Random;
import java.time.format.DateTimeFormatter;

@Service
public class LupaPasswordServiceImpl implements LupaPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenResetPasswordRepository TokenResetPasswordRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public void requestPasswordReset(LupaPasswordRequestDto request) {

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semua Field Harus Diisi");
        }

        if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username Minimal 3 Karakter Dan Maksimal 20 Karakter");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pengguna Tidak Ditemukan"));

        TokenResetPasswordRepository.findByUsername(user.getUsername()).ifPresent(existingToken -> {
            if (existingToken.getTanggalKadaluarsa().isAfter(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "OTP Masih Berlaku, Silakan Periksa Kembali Email Anda");
            } else {
                TokenResetPasswordRepository.deleteByUsername(user.getUsername());
            }
        });

        String otp = String.format("%06d", new Random().nextInt(999999));
        String hashedOtp = OtpUtil.hashOtp(otp);

        TokenResetPassword tokenBaru = TokenResetPassword.builder()
                .username(user.getUsername())
                .token(hashedOtp)
                .tanggalKadaluarsa(LocalDateTime.now().plusMinutes(5))
                .build();

        TokenResetPasswordRepository.save(tokenBaru);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
        String formatTanggalKadaluarsa = tokenBaru.getTanggalKadaluarsa().format(formatter);

        emailService.kirimOtpEmail(user.getEmail(), "OTP Reset Password", otp, formatTanggalKadaluarsa,
                user.getUsername());
    }

    @Override
    public void resetPassword(ResetPasswordDto request) {

        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPasswordLama() == null || request.getPasswordLama().isBlank()
                || request.getPasswordBaru() == null || request.getPasswordBaru().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semua Field Harus Diisi");
        }
        if (request.getUsername().length() < 3 || request.getUsername().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username Minimal 3 Karakter Dan Maksimal 20 Karakter");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Tidak Ditemukan"));

        if (!PasswordUtil.check(request.getPasswordLama(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password Lama Yang Anda Masukkan Salah");
        }
        if (PasswordUtil.check(request.getPasswordBaru(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password Yang Baru Tidak Boleh Sama Dengan Password Yang Lama");
        }

        if (!PasswordUtil.validasiPassword(user.getUsername(), request.getPasswordBaru())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password tidak memenuhi kriteria yg valid, minimal 8 karakter, kombinasi huruf, angka, karakter khusus, tanpa spasi");
        }

        user.setPassword(PasswordUtil.hash(request.getPasswordBaru()));
        userRepository.save(user);
    }

    @Override
    public void validasiOtpDanResetPassword(OtpResetPasswordDto dto) {

        if (dto.getUsername() == null || dto.getUsername().isBlank()
                || dto.getOtp() == null || dto.getOtp().isBlank()
                || dto.getPasswordBaru() == null || dto.getPasswordBaru().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semua Field Harus Diisi");

        }

        if (dto.getUsername().length() < 3 || dto.getUsername().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username Minimal 3 Karakter Dan Maksimal 20 Karakter");
        }

        if (!dto.getOtp().matches("\\d{6}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP Hanya Terdiri Dari 6 Angka Saja");
        }

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Tidak Ditemukan"));

        TokenResetPassword token = TokenResetPasswordRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP Tidak Ditemukan"));

        if (token.getTanggalKadaluarsa().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP Sudah Kadaluarsa");
        }

        if (!OtpUtil.checkOtp(dto.getOtp(), token.getToken())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP Yang Anda Masukkan Tidak Valid");
        }

        if (PasswordUtil.check(dto.getPasswordBaru(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password Yang Baru Tidak Boleh Sama Dengan Password Yang Lama");
        }
        if (!PasswordUtil.validasiPassword(user.getUsername(), dto.getPasswordBaru())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password tidak memenuhi kriteria yg valid, minimal 8 karakter, kombinasi huruf, angka, karakter khusus, tanpa spasi");
        }

        user.setPassword(PasswordUtil.hash(dto.getPasswordBaru()));
        userRepository.save(user);

        TokenResetPasswordRepository.deleteByUsername(dto.getUsername());
    }
}