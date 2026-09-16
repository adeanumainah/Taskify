package com.uas.java1.controller;

import java.io.IOException;
import java.util.Map;


import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uas.java1.exception.TidakDitemukan;
import com.uas.java1.model.TaskFile;
import com.uas.java1.repository.TaskFileRepository;
import com.uas.java1.service.TaskFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/task-files")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class TaskFileController {

    private final TaskFileService taskFileService;
    private final TaskFileRepository taskFileRepository;

    @PostMapping(value = "/{taskId}/kirim-tugas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> kirimFile(@PathVariable Long taskId, @RequestParam("file") MultipartFile file)
            throws IOException {
        taskFileService.uploadFile(taskId, file);
        return ResponseEntity.ok(Map.of("pesan", "File Tugas anda berhasil di Kirim ke task ID : " + taskId));
    }

    @GetMapping("/{fileId}/ambil-tugas-yang-sudah-dikirim")
    public ResponseEntity<?> ambilFileTugas(@PathVariable Long fileId) {
        TaskFile fileTugas = taskFileRepository.findById(fileId)
                .orElseThrow(() -> new TidakDitemukan("File tidak di temukan"));
        byte[] fileData = fileTugas.getFileData();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileTugas.getFileName())
                .build());
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }

}
