package com.uas.java1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uas.java1.exception.FileKirimFileException;
import com.uas.java1.model.Task;
import com.uas.java1.model.TaskFile;
import com.uas.java1.repository.TaskFileRepository;
import com.uas.java1.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskFileService {

    private final TaskRepository taskRepository;
    private final TaskFileRepository taskFileRepository;

    private final String uploadFolder = "uploads/";

    public void uploadFile(Long taskId, MultipartFile file) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tugas tidak ditemukan"));

        try {

            String namaFileOriginal = file.getOriginalFilename();
            String namaFileYangDisimpan = UUID.randomUUID() + "_" + namaFileOriginal;

            byte[] fileData = file.getBytes();

            TaskFile fileTugas = TaskFile.builder()
                    .task(task)
                    .fileName(namaFileOriginal)
                    .filePath(namaFileYangDisimpan)
                    .fileData(fileData)
                    .uploadedAt(LocalDateTime.now()).build();
            taskFileRepository.save(fileTugas);

            task.setStatus("COMPLETED");
            taskRepository.save(task);

            File folder = new File(uploadFolder);
            if (!folder.exists()) {
            folder.mkdirs();
            }
            Path pathFile = Paths.get(uploadFolder, namaFileYangDisimpan);
            Files.write(pathFile, fileData);
        } catch (IOException error) {
            throw new FileKirimFileException("Gagal mengirim Tugas : " + error.getMessage());
        }
    }

    public List<TaskFile> getFilesByTaskId(Long taskId) {
        return taskFileRepository.findByTaskId(taskId);
    }
}
