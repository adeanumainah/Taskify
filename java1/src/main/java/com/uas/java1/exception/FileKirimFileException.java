package com.uas.java1.exception;

public class FileKirimFileException extends RuntimeException {
    public FileKirimFileException(String pesan){
        super(pesan);
    }

    public FileKirimFileException(String pesan, Throwable penyebabError){
        super(pesan,penyebabError);
    }
}