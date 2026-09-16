package com.uas.java1.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uas.java1.dto.TaskDto;
import com.uas.java1.model.Task;
import com.uas.java1.model.TaskFile;
import com.uas.java1.service.TaskFileExportService;
import com.uas.java1.service.TaskFileService;
import com.uas.java1.service.TaskService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

    private final TaskService taskService;
    private final TaskFileService taskFileService;
    private final TaskFileExportService taskFileExportService;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    public Task createTask(@RequestBody TaskDto task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{taskId}/files")
    public List<TaskFile> getTaskFiles(@PathVariable Long taskId) {
        return taskFileService.getFilesByTaskId(taskId);
    }

    @GetMapping("/export-laporan-tugas")
    public void exportLaporanTugas(@RequestParam int tahun, @RequestParam int bulan, HttpServletResponse response) throws IOException{
        taskFileExportService.exportToExcellLaporan(tahun, bulan, response);
    }

}