package com.uas.java1.exception;

public class FileProsesError extends RuntimeException{
    public FileProsesError(String pesan) {
        super(pesan);
    }
}