package com.uas.java1.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.uas.java1.exception.FileKirimFileException;
import com.uas.java1.exception.FileProsesError;
import com.uas.java1.exception.TidakDitemukan;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(FileKirimFileException.class)
    public ResponseEntity<?> handleKirimFileException(FileKirimFileException error) {
        Map<String, Object> pesanError = Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "pesan",
                error.getMessage(), "stempelWaktu", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(pesanError);
    }
    @ExceptionHandler(TidakDitemukan.class)
    public ResponseEntity<?> handleTidakDitemukan(TidakDitemukan error) {
        Map<String, Object> pesanError = Map.of("status", HttpStatus.NOT_FOUND.value(), "pesan",
                error.getMessage(), "stempelWaktu", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(pesanError);
    }
    @ExceptionHandler(FileProsesError.class)
    public ResponseEntity<?> handleProsesFileError(FileProsesError error) {
        Map<String, Object> pesanError = Map.of("status", HttpStatus.BAD_REQUEST.value(), "pesan",
                error.getMessage(), "stempelWaktu", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(pesanError);
    }
}
