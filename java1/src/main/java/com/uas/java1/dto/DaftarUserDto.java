package com.uas.java1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DaftarUserDto {
    private String username;
    private String password;
    private String email;
    private String namaLengkap;
}
