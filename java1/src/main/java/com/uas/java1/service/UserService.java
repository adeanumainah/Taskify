package com.uas.java1.service;

import com.uas.java1.dto.DaftarUserDto;
import com.uas.java1.dto.UserDetailDto;

public interface UserService {
    UserDetailDto daftar(DaftarUserDto dto);
}
