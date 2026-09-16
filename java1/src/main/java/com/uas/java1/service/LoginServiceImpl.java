package com.uas.java1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.uas.java1.dto.LoginDto;
import com.uas.java1.dto.UserDetailDto;
import com.uas.java1.model.Role;
import com.uas.java1.model.User;
import com.uas.java1.provider.JwtProvider;
import com.uas.java1.repository.UserRepository;
import com.uas.java1.util.PasswordUtil;

import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public UserDetailDto login(LoginDto dto) {

        if (dto.getUsername() == null || dto.getUsername().trim().isBlank()
                || dto.getPassword() == null || dto.getPassword().trim().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semua Field Harus Diisi");
        }
        
        if (dto.getUsername().length() < 3 || dto.getUsername().length() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username Minimal 3 Karakter Dan Maksimal 20 Karakter");
        }
        
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Username Atau Password Tidak Valid"));


        if (!PasswordUtil.check(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username Atau Password Tidak Valid");
        }

        List<String> role = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        String aksesToken = jwtProvider.generateAccessToken(user.getId(), user.getUsername(), role);

        return UserDetailDto.builder()
                .username(user.getUsername())
                .role(String.join(",", role))
                .namaLengkap(user.getNamaLengkap())
                .aksesToken(aksesToken)
                .build();
    }
}