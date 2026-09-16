package com.uas.java1.exception;

public class TidakDitemukan extends RuntimeException {
    public TidakDitemukan(String pesan){
        super(pesan);
    }
}