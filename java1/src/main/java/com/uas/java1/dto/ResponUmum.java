package com.uas.java1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponUmum<T> {
    private boolean berhasil;
    private String pesan;
    private T data;
    
}