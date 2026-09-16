package com.uas.java1.service;

import com.uas.java1.dto.LoginDto;
import com.uas.java1.dto.UserDetailDto;

public interface LoginService {
    UserDetailDto login(LoginDto dto);
}